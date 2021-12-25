package application.servlets;

import adapter.controller.UserController;
import application.services.HttpService;
import application.services.json.JsonService;
import domain.entity.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Collections;

import static config.MyConfiguration.userController;

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
        userController.uploadPhotosContent(user.getCard().getPhotos());
        
        User me = (User) req.getSession().getAttribute("user");
        userController.leadLastActionToLocationTimeUser(Collections.singletonList(user), me.getLocation());
        HttpService.putBody(resp, JsonService.getJsonWithExposeFields(user));
    }
}
