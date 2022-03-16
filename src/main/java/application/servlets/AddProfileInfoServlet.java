package application.servlets;

import adapter.controller.UserController;
import application.services.HttpService;
import application.services.MatchUtils;
import application.services.json.JsonService;
import config.MyProperties;
import domain.entity.Photo;
import domain.entity.User;
import domain.entity.UserCard;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static config.MyConfiguration.userController;

public class AddProfileInfoServlet extends HttpServlet {

    private UserController userController;

    private final static String JPG = "jpg";
    private static final List<String> compressRequiredFormats = Arrays.asList("png", "tiff", "psd", "bmp", "hdr", "jpeg", "tga", "webp", "sgi");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        req.setCharacterEncoding("UTF-8");

        String act = req.getParameter("act");
        User user = (User) req.getSession().getAttribute("user");

        switch (act) {
            case "card":
                UserCard newCard = (UserCard) JsonService.getObjectByExposeFields(UserCard.class, HttpService.getBody(req));
                newCard.setPhotos(user.getCard().getPhotos());
                newCard.setId(user.getCard().getId());
                newCard.setUserId(user.getId());

                user.setCard(newCard);
                userController.updateUserCard(newCard);
                break;

            case "photo":
                List<Photo> photos = JsonService.getPhotoList(HttpService.getBody(req));
                if (photos != null && !photos.isEmpty()) {
                    if (photos.stream().filter(Photo::isMain).count() > 1) {
                        HttpService.putBody(resp, "EXPECTED ONLY 1 MAIN PHOTO");
                        return;
                    }
                    if (!processActionPhoto(photos, user)) {
                        HttpService.putBody(resp, "UNEXPECTED PHOTO FORMAT");
                        return;
                    }
                    List<Photo> currentPhotoList = user.getCard().getPhotos().stream().filter(Objects::nonNull).collect(Collectors.toList());
                    String photoParams = currentPhotoList.stream().map(ph -> String.format("%s_%s", ph.getNumber(), ph.getFormat())).collect(Collectors.joining(";"));
                    Integer mainPhoto = currentPhotoList.stream().filter(Photo::isMain).findFirst().map(ph -> Integer.parseInt(ph.getNumber())).orElse(null);

                    if (mainPhoto != null || currentPhotoList.isEmpty()) {
                        userController.updateMainPhoto(user.getCard().getId(), mainPhoto); 
                    }
                    userController.updatePhotoParams(user.getCard().getId(), photoParams);
                    req.getSession().setAttribute("user", user);
                    HttpService.putBody(resp, JsonService.getJsonArrayWithExpose(currentPhotoList));
                }
                break;

            default:
                HttpService.putBody(resp, "UNEXPECTED ACTION");
        }
    }

    private boolean processActionPhoto(List<Photo> photos, User user) {
        boolean mainPhotoHasBeenChange = false;
        String mainNumber = photos.stream().filter(Objects::nonNull).findFirst().map(Photo::getNumber).orElse(null);

        List<Photo> current = Optional.ofNullable(user.getCard().getPhotos()).orElse(new ArrayList<>(Collections.nCopies(6, null)));
        // current.stream().filter(Objects::nonNull).filter(Photo::isMain).findFirst().ifPresent(photo -> photo.setMain(false));

        for (Photo photo : photos) {
            String path = getPhotoPath(user, photo.getNumber(), false);
            int index = Optional.ofNullable(photo.getNumber()).map(Integer::parseInt).orElse(0) - 1;
            if (index < 0)
                continue;

            switch (photo.getAction()) {
                case "save":
                    try {
//                        if (checkAndDeleteIfMain(user, photo, mainNumber)) {
//                            mainPhotoHasBeenChange = true;
//                            mainNumber = photo.getNumber();
//                        }

                        byte[] byteContent = photo.getContent().getBytes();
                        if (JPG.equals(photo.getFormat())) {
                            ByteArrayInputStream bufferedInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(byteContent));
                            BufferedImage img = ImageIO.read(bufferedInputStream);
                            img = cutRegularPhoto(img);

                            ImageIO.write(img, photo.getFormat(), new File(path));
                            byteContent = imageToByteArray(img);
                        }
                        else if (compressRequiredFormats.contains(photo.getFormat()))
                            byteContent = compressImage(byteContent, path);
                        else
                            return false;

                        photo.setFormat(JPG);
                        photo.setContent(new String(Base64.getEncoder().encode(byteContent)));
                        current.set(index, photo);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "delete":
                    if (checkAndDeleteIfMain(user, photo, mainNumber)) {
                        mainPhotoHasBeenChange = true;
                        mainNumber = photo.getNumber();
                    }
                    File file = new File(path);
                    if (file.exists() && file.delete()) {
                        current.set(index, null);
                    }
                default:
                    break;
            }
        }

        Optional<Photo> mainPhoto = current.stream().filter(Objects::nonNull).filter(Photo::isMain).findFirst();
        if (!mainPhoto.isPresent()) {
            mainPhoto = current.stream().filter(Objects::nonNull).findFirst();
            if (mainPhoto.isPresent()) {
                mainPhoto.get().setMain(true);
                mainNumber = mainPhoto.get().getNumber();
                mainPhotoHasBeenChange = true;
            }
        }

        if (mainPhotoHasBeenChange) {
            doNewMainPhoto(user, mainNumber);
            current.add(5, mainPhoto.get());
        }

        user.getCard().setPhotos(current);
        return true;
    }

    private byte[] compressImage(byte[] content, String destinationPath) throws IOException {
        ByteArrayInputStream bufferedInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(content));
        BufferedImage img = ImageIO.read(bufferedInputStream);

        img = cutRegularPhoto(img);
        
        File compressedImageFile = new File(destinationPath);
        OutputStream os = new FileOutputStream(compressedImageFile);

        Iterator<ImageWriter> writers =  ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = writers.next();

        ImageOutputStream ios = ImageIO.createImageOutputStream(os);
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();

        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(0.80f);
        writer.write( null , new IIOImage(img, null , null ), param);

        os.close();
        ios.close();
        writer.dispose();

        return imageToByteArray(img);
    }

    private BufferedImage cutRegularPhoto(BufferedImage img) {
        int onePart = getOnePart(img.getHeight(), img.getWidth(), 3, 2);
        double width = onePart * 2;
        double height = onePart * 3;
        int x = (int) ((img.getWidth() - width) / 2);
        int y = (int) ((img.getHeight() - height) / 2);
        return img.getSubimage(x, y, (int) width, (int) height);
    }

    private byte[] imageToByteArray(BufferedImage img) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, JPG, baos);
        return baos.toByteArray();
    }
    
    private boolean checkAndDeleteIfMain(User user, Photo photo, String oldMain) {
        if (photo.isMain() && photo.getNumber().equals(oldMain)) {
            String mainPhotoPath = getPhotoPath(user, photo.getNumber(), true);
            File oldMainFile = new File(mainPhotoPath);
            if (oldMainFile.exists() && oldMainFile.delete()) {
                System.out.println("Файл " + mainPhotoPath + " был удален");
            }
            return true;
        }
        return false;
    }

    private void doNewMainPhoto(User user, String newMainNum) {
        String photoPath =  getPhotoPath(user, newMainNum, false);
        String newMainPath = getPhotoPath(user, newMainNum, true);

        File file = new File(photoPath);
        try {
            byte[] fileInArray = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(photoPath);
            fis.read(fileInArray);

            ByteArrayInputStream bufferedInputStream = new ByteArrayInputStream(fileInArray);
            BufferedImage img = ImageIO.read(bufferedInputStream);

            int onePart = getOnePart(img.getHeight(), img.getWidth(), 1, 1);
            int x = (img.getWidth() - onePart) / 2;
            int y = (img.getHeight() - onePart) / 2;
            img = img.getSubimage(x, y, onePart, onePart);

            ImageIO.write(img, "jpg", new File(newMainPath));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    private int getOnePart(int height, int width, int heightParts, int widthParts) {
        if (height < 3)
            return -1;
        if (width < 2)
            return -1;

        int onePart;
        double x = ((double) widthParts / heightParts) * height;

        if (x * widthParts <= width)
            onePart = (int) x / widthParts;
        else 
            onePart = width / widthParts;
        
        while (onePart * heightParts > height)
            onePart--;
        while (onePart * widthParts > width)
            onePart--;
        
        return onePart;
    }

    private String getPhotoPath(User user, String photoNum, boolean main) {
        return String.format("%sIMG_%s%s_photo_%s.jpg", MyProperties.IMAGES_PATH + MatchUtils.getSlash(), main ? "MAIN_" : "", user.getId(), photoNum);
    }

    @Override
    public void init() {
        userController = userController();
    }
}
