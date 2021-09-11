package application.servlet;

import adapter.controller.UserController;
import application.services.HttpService;
import application.services.json.JsonService;
import config.MyConfiguration;
import domain.entity.User;
import domain.entity.model.types.Action;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Optional;


public class LikeServlet extends HttpServlet {
    private UserController userController;

    @Override
    public void init() {
        userController = MyConfiguration.userController();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        User user = (User) req.getSession().getAttribute("user");

        String body = HttpService.getBody(req);
        int toUserId = Optional.ofNullable(JsonService.getParameter(body, "toUserId")).map(Integer::parseInt).orElse(-1);
        String action = JsonService.getParameter(body,"action");
        if (action == null || toUserId < 0)
            return;

        switch (action) {
            case "match":
                userController.match(user.getId(), toUserId);
                break;
            case "like":
                userController.like(user.getId(), toUserId);
                break;
            case "dislike":
                userController.deleteLike(user.getId(), toUserId);
                break;
            default:
                HttpService.putBody(resp, "UNEXPECTED ACTION PARAMETER");
                return;
        }

        HttpService.putBody(resp,"SUCCESS");
    }
}
