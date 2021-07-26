package application.servlet;

import adapter.controller.UserController;
import application.services.HttpService;
import application.services.json.JsonService;
import config.MyProperties;
import domain.entity.Photo;
import domain.entity.User;
import domain.entity.UserCard;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import java.util.Base64;
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
                UserCard card = (UserCard) JsonService.getObject(UserCard.class, HttpService.getBody(req));
                card.setId(user.getCard().getId());
                user.setCard(userController.updateUserCard(card));
                break;
            case  "photo":
                List<Photo> photos = JsonService.getList(HttpService.getBody(req));
                if (photos != null && !photos.isEmpty()) {
                    processActionPhoto(photos, user.getId());
                    userController.updatePhotoParams(user.getId(), photos);
                }
        }
    }

    private void processActionPhoto(List<Photo> photos, int id) {
        for (Photo photo : photos) {
            String path = String.format("%sIMG_%s_%s_%s.%s", MyProperties.IMAGES_PATH, id, "photo", photo.getNumber(), photo.getFormat());

            switch (photo.getAction()) {
                case "save":
                    try {
                        byte[] byteContent = photo.getContent().getBytes();
                        ByteArrayInputStream bufferedInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(byteContent));
                        BufferedImage img = ImageIO.read(bufferedInputStream);
                        ImageIO.write(img, photo.getFormat(), new File(path));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "delete":
                    File file = new File(path);
                    if (file.exists() && file.delete()) {
                        return;
                    }
            }
        }
    }

    @Override
    public void init() {
        userController = userController();
    }
}
