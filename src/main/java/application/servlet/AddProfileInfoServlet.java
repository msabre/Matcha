package application.servlet;

import adapter.controller.UserController;
import application.services.HttpService;
import application.services.json.JsonService;
import config.MyProperties;
import domain.entity.User;
import domain.entity.UserCard;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static config.MyConfiguration.userController;

public class AddProfileInfoServlet extends HttpServlet {

    private UserController userController;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        try {
            int id = Integer.parseInt(req.getParameter("id"));
            User user = userController.findUser(id);
            HttpService.putBody(resp, JsonService.getJson(user));

        } catch (Exception e) {
            HttpService.putBody(resp, "WRONG ID");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        User user = (User) req.getSession().getAttribute("user");

        List<FileItem> items;
        List<File> photo = new ArrayList<>();
        try {
            items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(req);
            for (FileItem item : items) {
                if (!item.isFormField() && item.getFieldName().contains("photo") && item.getSize() > 0) {
                    String fileName = item.getName();
                    String format = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
                    String name = "IMG_" + user.getId() + "_" + item.getFieldName() + "." + format;

                    BufferedImage image = ImageIO.read(item.getInputStream());
                    File file = new File(MyProperties.IMAGES_PATH + name);
                    ImageIO.write(image, format, file);

                    photo.add(file);
                }
            }
        } catch(FileUploadException e){
                e.printStackTrace();
        }

        UserCard card = (UserCard) JsonService.getObjectWithExposeFields(UserCard.class, HttpService.getBody(req));

        if (!photo.isEmpty())
            card.setPhotos(photo);

        card.setId(user.getCard().getId());
        user.setCard(userController.updateUserCard(card));
    }

    @Override
    public void init() throws ServletException {
        userController = userController();
    }
}
