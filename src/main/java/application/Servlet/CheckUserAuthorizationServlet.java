package application.Servlet;


import adapter.controller.JwtController;
import adapter.controller.UserController;
import application.services.HttpService;
import application.services.json.JsonService;
import domain.entity.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static config.MyConfiguration.jwtController;
import static config.MyConfiguration.userController;

public class CheckUserAuthorizationServlet extends HttpServlet {

    private UserController userController;
    private JwtController jwtController;

    @Override
    public void init() {
        userController = userController();
        jwtController = jwtController();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");

        int userId = jwtController.checkJwt(req, resp);
        if (userId >= 0) {
            User user = (User) req.getSession().getAttribute("user");
            if (user == null)
                user = userController.findUser(userId);
            String body = JsonService.getJson(user);

            HttpService.putBody(resp, body);
        }

        HttpService.putBody(resp, "WRONG");
    }
}
