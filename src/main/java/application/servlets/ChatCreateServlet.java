package application.servlets;

import adapter.controller.UserController;
import application.services.HttpService;
import application.services.json.JsonService;
import config.MyConfiguration;
import domain.entity.User;

import javax.servlet.http.*;
import java.io.IOException;
import java.util.Optional;

public class ChatCreateServlet extends HttpServlet {

    private UserController userController;

    @Override
    public void init() {
        userController = MyConfiguration.userController();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");

        String body = HttpService.getBody(req);
        int toUsr = Integer.parseInt(Optional.ofNullable(JsonService.getParameter(body, "toUsr")).orElse(""));
        if (toUsr < 0) {
            HttpService.putBody(resp, "wrong 'toUsr' param");
            return;
        }

        User user = (User) req.getSession().getAttribute("user");
        int chatId = userController.createChatBetweenTwoUsers(user.getId(), toUsr);
        if (chatId < 0) {
            HttpService.putBody(resp, "ERROR");
            return;
        }
        HttpService.putBody(resp, String.valueOf(chatId));
    }

}
