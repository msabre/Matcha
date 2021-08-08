package application.servlet;


import adapter.controller.JwtController;
import adapter.controller.UserController;
import application.services.HttpService;
import application.services.json.JsonService;
import domain.entity.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static config.MyConfiguration.jwtController;

public class GetAuthorizedUserServlet extends HttpServlet {

    private JwtController jwtController;

    @Override
    public void init() {
        jwtController = jwtController();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setCharacterEncoding("UTF-8");

        if (jwtController.checkJwt(req, resp)) {
            User user = (User) req.getSession().getAttribute("user");
            String body = JsonService.getJson(user);
            HttpService.putBody(resp, body);

            return;
        }

        HttpService.putBody(resp, "Error JWT");
    }
}
