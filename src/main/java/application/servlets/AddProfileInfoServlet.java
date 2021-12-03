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
import java.awt.*;
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
                }
                break;

            default:
                HttpService.putBody(resp, "UNEXPECTED ACTION");
        }
    }

    private boolean processActionPhoto(List<Photo> photos, User user) {
        boolean mainPhotoHasBeenChange = false;
        String mainNumber = photos.stream().filter(Objects::nonNull).findFirst().map(Photo::getNumber).orElse(null);

        List<Photo> current = Optional.ofNullable(user.getCard().getPhotos()).orElse(new ArrayList<>(Collections.nCopies(5, null)));
        current.stream().filter(Objects::nonNull).filter(Photo::isMain).findFirst().ifPresent(photo -> photo.setMain(false));

        for (Photo photo : photos) {
            String path = getPhotoPath(user, photo.getNumber(), false);
            int index = Optional.ofNullable(photo.getNumber()).map(Integer::parseInt).orElse(0) - 1;
            if (index < 0)
                continue;

            switch (photo.getAction()) {
                case "save":
                    try {
                        if (checkAndDeleteIfMain(user, photo, mainNumber)) {
                            mainPhotoHasBeenChange = true;
                            mainNumber = photo.getNumber();
                        }

                        byte[] byteContent = photo.getContent().getBytes();
                        if (JPG.equals(photo.getFormat())) {
                            ByteArrayInputStream bufferedInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(byteContent));
                            BufferedImage img = ImageIO.read(bufferedInputStream);
     
                            ImageIO.write(img, photo.getFormat(), new File(path));
                        }
                        else if (compressRequiredFormats.contains(photo.getFormat()))
                            compressImage(byteContent, path, 600, 800);
                        else
                            return false;

                        photo.setFormat(JPG);
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
            if (mainPhoto.isPresent())
                mainNumber = mainPhoto.get().getNumber();
        }

        if (mainPhotoHasBeenChange)
            doNewMainPhoto(user, mainNumber);

        return true;
    }

    private static void compressImage(byte[] content, String destinationPath, int width, int height) throws IOException {
        ByteArrayInputStream bufferedInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(content));
        BufferedImage img = ImageIO.read(bufferedInputStream);

        Image resizeImage = img.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        File compressedImageFile = new File(destinationPath);
        OutputStream os = new FileOutputStream(compressedImageFile);

        Iterator<ImageWriter> writers =  ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = writers.next();

        ImageOutputStream ios = ImageIO.createImageOutputStream(os);
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();

        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(0.08f);
        writer.write( null , new IIOImage(convertToBufferedImage(resizeImage), null , null ), param);

        os.close();
        ios.close();
        writer.dispose();
    }

    public static BufferedImage convertToBufferedImage(Image img) {

        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bi = new BufferedImage(
                img.getWidth(null), img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = bi.createGraphics();
        graphics2D.drawImage(img, 0, 0, null);
        graphics2D.dispose();

        return bi;
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
            Image resizeImage = img.getScaledInstance(400, 400, Image.SCALE_DEFAULT);

            ImageIO.write(convertToBufferedImage(resizeImage), "jpg", new File(newMainPath));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String getPhotoPath(User user, String photoNum, boolean main) {
        return String.format("%sIMG_%s_%s_photo_%s.jpg", MyProperties.IMAGES_PATH + MatchUtils.getSlash(), main ? "MAIN" : "", user.getId(), photoNum);
    }

    @Override
    public void init() {
        userController = userController();
    }
}
