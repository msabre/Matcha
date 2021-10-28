package application.servlets;


import adapter.controller.JwtController;
import adapter.controller.UserController;
import application.services.HttpService;
import application.services.json.JsonService;
import domain.entity.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static config.MyConfiguration.jwtController;
import static config.MyConfiguration.userController;

public class GetAuthorizedUserServlet extends HttpServlet {
    private JwtController jwtController;
    private UserController userController;

    @Override
    public void init() {
        jwtController = jwtController();
        userController = userController();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setCharacterEncoding("UTF-8");

        if (jwtController.checkJwt(req, resp)) {
            User user = (User) req.getSession().getAttribute("user");
            userController.uploadPhotosContent(user.getCard().getPhotos());
            HttpService.putBody(resp, JsonService.getJsonWithExposeFields(user));

            return;
        }

        HttpService.putBody(resp, "Error JWT");
    }
}
