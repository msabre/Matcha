package application.servlets;

import adapter.controller.UserController;
import application.services.HttpService;
import application.services.json.JsonService;
import config.MyConfiguration;
import domain.entity.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class ChatDeleteServlet extends HttpServlet {

    private UserController userController;

    @Override
    public void init() {
        userController = MyConfiguration.userController();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");

        String body = HttpService.getBody(req);
        int chatId = Integer.parseInt(Optional.ofNullable(JsonService.getParameter(body, "chatId")).orElse("-1"));
        int userId = ((User) req.getSession().getAttribute("user")).getId();

        if (chatId < 0) {
            HttpService.putBody(resp, "'chatId' VALUE MUST BE MORE 0");
            return;
        }
        if (userController.deleteChat(chatId, userId))
            HttpService.putBody(resp, "SUCCESS");
        else
            HttpService.putBody(resp, "WRONG");
    }
}
