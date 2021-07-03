package application.Servlet;

import adapter.controller.JwtController;
import adapter.controller.UserController;
import application.services.HttpService;
import application.services.json.JsonService;
import config.MyConfiguration;
import config.MyProperties;
import domain.entity.User;
import domain.entity.UserCard;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import sun.net.httpserver.HttpsServerImpl;

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

public class LogoutServlet extends HttpServlet {

    private JwtController jwtController;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        Integer id = Integer.parseInt(req.getParameter("id"));

        if (id < 0) {
            jwtController.removeTokenByUserId(id);
        } else {
            HttpService.putBody(resp, "WRONG");
        }
    }

    @Override
    public void init() throws ServletException {
        jwtController = MyConfiguration.jwtController();
    }
}
