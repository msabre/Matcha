package application.servlets;

import adapter.controller.UserController;
import application.services.HttpService;
import application.services.json.JsonService;
import config.MyConfiguration;
import domain.entity.LikeAction;
import domain.entity.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;


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
        LikeAction likeAction = (LikeAction) JsonService.getObject(LikeAction.class, body);
        if (likeAction.getAction() == null || likeAction.getToUsr() < 0)
            return;

        likeAction.setFromUsr(user.getId());
        switch (likeAction.getAction()) {
            case LIKE:
                boolean isMatch = userController.putMatchOrLike(likeAction);
                if (isMatch) {
                    HttpService.putBody(resp, "MATCH");
                    return;
                }
                break;

            case DISLIKE:
                userController.disLike(likeAction);
                break;
            case TAKE_LIKE:
                userController.takeLike(likeAction);
                break;
            case VISIT:
                userController.visit(likeAction);
                break;

            case BLOCK:
                userController.block(likeAction);
                break;

            case FAKE:
                if (userController.fake(likeAction)) {
                    HttpService.putBody(resp, "BANNED");
                    return;
                }
                break;

            case TAKE_FAKE:
                userController.takeFake(likeAction);
                break;
            default:
                HttpService.putBody(resp, "UNEXPECTED ACTION PARAMETER");
                return;
        }

        HttpService.putBody(resp,"SUCCESS");
    }
}
