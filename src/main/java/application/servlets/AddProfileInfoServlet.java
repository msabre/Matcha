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
                UserCard card = (UserCard) JsonService.getObjectByExposeFields(UserCard.class, HttpService.getBody(req));
                card.setId(user.getCard().getId());
                user.setCard(userController.updateUserCard(card));
                break;
            case "photo":
                List<Photo> photos = JsonService.getList(HttpService.getBody(req));
                if (photos != null && !photos.isEmpty()) {
                    if (!processActionPhoto(photos, user)) {
                        HttpService.putBody(resp, "UNEXPECTED PHOTO FORMAT");
                        return;
                    }
                    userController.updatePhotoParams(user.getCard().getId(), photos);
                }
        }
    }

    private boolean processActionPhoto(List<Photo> photos, User user) {
        List<Photo> current = Optional.ofNullable(user.getCard().getPhotos()).orElse(new ArrayList<>(Collections.nCopies(5, null)));

        for (Photo photo : photos) {
            String path = String.format("%sIMG_%s_%s_photo.jpg", MyProperties.IMAGES_PATH + MatchUtils.getSlash(), user.getId(), photo.getNumber());
            int index = Optional.ofNullable(photo.getNumber()).map(Integer::parseInt).orElse(0) - 1;
            if (index < 0)
                continue;

            switch (photo.getAction()) {
                case "save":
                    try {
                        byte[] byteContent = photo.getContent().getBytes();
                        if (JPG.equals(photo.getFormat())) {
                            ByteArrayInputStream bufferedInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(byteContent));
                            BufferedImage img = ImageIO.read(bufferedInputStream);
                            ImageIO.write(img, photo.getFormat(), new File(path));
                        }
                        else if (compressRequiredFormats.contains(photo.getFormat()))
                            compressImage(byteContent, path);
                        else
                            return false;

                        current.set(index, photo);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "delete":
                    File file = new File(path);
                    if (file.exists() && file.delete())
                        current.set(index, null);
                default:
                    break;
            }
        }
        return true;
    }


    private static void compressImage(byte[] content, String destinationPath) throws IOException {
        ByteArrayInputStream bufferedInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(content));
        BufferedImage img = ImageIO.read(bufferedInputStream);

        File compressedImageFile = new File(destinationPath);
        OutputStream os =new FileOutputStream(compressedImageFile);

        Iterator<ImageWriter> writers =  ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = writers.next();

        ImageOutputStream ios = ImageIO.createImageOutputStream(os);
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();

        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(0.05f);
        writer.write( null , new IIOImage(img, null , null ), param);

        os.close();
        ios.close();
        writer.dispose();
    }

    @Override
    public void init() {
        userController = userController();
    }
}