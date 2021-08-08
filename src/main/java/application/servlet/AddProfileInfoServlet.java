package application.servlet;

import adapter.controller.UserController;
import application.services.HttpService;
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

import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import static config.MyConfiguration.userController;

public class AddProfileInfoServlet extends HttpServlet {

    private UserController userController;

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
            case  "photo":
                List<Photo> photos = JsonService.getList(HttpService.getBody(req));
                if (photos != null && !photos.isEmpty()) {
                    processActionPhoto(photos, user);
                    userController.updatePhotoParams(user.getId(), photos);
                }
        }
    }

    private void processActionPhoto(List<Photo> photos, User user) {
        List<Photo> currentPhotos = user.getCard().getPhotos();
        for (Photo photo : photos) {
            String path = String.format("%sIMG_%s_%s_%s.%s", MyProperties.IMAGES_PATH, user.getId(), "photo", photo.getNumber(), photo.getFormat());
            int index = Integer.parseInt(photo.getNumber()) - 1;

            switch (photo.getAction()) {
                case "save":
                    try {
                        byte[] byteContent = photo.getContent().getBytes();
                        if (photo.getFormat().equals("png") || photo.getFormat().equals("jpg")) {
                            ByteArrayInputStream bufferedInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(byteContent));
                            BufferedImage img = ImageIO.read(bufferedInputStream);
                            ImageIO.write(img, photo.getFormat(), new File(path));
                        }
                        else
                            compressImage(byteContent, path);

                        if (currentPhotos.get(index) != null) currentPhotos.set(index, photo); else currentPhotos.add(photo);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "delete":
                    File file = new File(path);
                    if (file.exists() && file.delete()) {
                        user.getCard().getPhotos().remove(index);
                        return;
                    }
            }
        }
    }

    public void compressImage(byte[] content, String destinationPath) throws IOException {
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


// 2D Example
//class JavaImageResizer {
//
//    public static void main(String[] args) throws IOException {
//
//        File folder = new File("/Users/pankaj/Desktop/images");
//        File[] listOfFiles = folder.listFiles();
//        System.out.println("Total No of Files:"+listOfFiles.length);
//        Image img = null;
//        BufferedImage tempPNG = null;
//        BufferedImage tempJPG = null;
//        File newFilePNG = null;
//        File newFileJPG = null;
//        for (int i = 0; i < listOfFiles.length; i++) {
//            if (listOfFiles[i].isFile()) {
//                System.out.println("File " + listOfFiles[i].getName());
//                img = ImageIO.read(new File("/Users/pankaj/Desktop/images/"+listOfFiles[i].getName()));
//                tempPNG = resizeImage(img, 100, 100);
//                tempJPG = resizeImage(img, 100, 100);
//                newFilePNG = new File("/Users/pankaj/Desktop/images/resize/"+listOfFiles[i].getName()+"_New.png");
//                newFileJPG = new File("/Users/pankaj/Desktop/images/resize/"+listOfFiles[i].getName()+"_New.jpg");
//                ImageIO.write(tempPNG, "png", newFilePNG);
//                ImageIO.write(tempJPG, "jpg", newFileJPG);
//            }
//        }
//        System.out.println("DONE");
//    }
//
//    /**
//     * This function resize the image file and returns the BufferedImage object that can be saved to file system.
//     */
//    public static BufferedImage resizeImage(final Image image, int width, int height) {
//        final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//        final Graphics2D graphics2D = bufferedImage.createGraphics();
//        graphics2D.setComposite(AlphaComposite.Src);
//        //below three lines are for RenderingHints for better image quality at cost of higher processing time
//        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
//        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
//        graphics2D.drawImage(image, 0, 0, width, height, null);
//        graphics2D.dispose();
//        return bufferedImage;
//    }
//}
