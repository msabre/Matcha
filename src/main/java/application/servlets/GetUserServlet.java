package application.servlets;

import adapter.controller.UserController;
import application.services.HttpService;
import application.services.json.JsonService;
import domain.entity.FilterParams;
import domain.entity.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static config.MyConfiguration.userController;
import static java.util.Objects.isNull;

public class GetUserServlet extends HttpServlet {

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

        User user = userController.findUser(id);
        HttpService.putBody(resp, JsonService.getJson(user));
    }
}
