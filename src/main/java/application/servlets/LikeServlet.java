package application.servlets;

import adapter.controller.UserController;
import application.services.HttpService;
import application.services.json.JsonService;
import config.MyConfiguration;
import domain.entity.User;

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
            case "like":
                boolean isMatch = userController.putMatchOrLike(user.getId(), toUserId);
                if (isMatch) {
                    HttpService.putBody(resp, "MATCH");
                    return;
                }
                break;
            case "takeLike":
                userController.deleteLike(user.getId(), toUserId);
                break;
                // TODO добавить дизлайк в онлайне (не дизить заранее)
            default:
                HttpService.putBody(resp, "UNEXPECTED ACTION PARAMETER");
                return;
        }

        HttpService.putBody(resp,"SUCCESS");
    }
}