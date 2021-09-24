package application.servlets;


import adapter.controller.UserController;
import application.services.HttpService;
import application.services.json.JsonService;
import domain.entity.Photo;
import domain.entity.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static config.MyConfiguration.userController;


public class GetIconServlet extends HttpServlet {

    private UserController userController;

    @Override
    public void init() {
        userController = userController();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setCharacterEncoding("UTF-8");

        int id = Integer.parseInt(req.getParameter("id"));
        if (id < 0) {
            HttpService.putBody(resp, "WRONG");
            return;
        }

        Photo photo = userController.getUserIcon(id);
        if (photo != null) {
            HttpService.putBody(resp, JsonService.getJson(photo));
            return;
        }
        HttpService.putBody(resp, "WRONG");
    }
}
