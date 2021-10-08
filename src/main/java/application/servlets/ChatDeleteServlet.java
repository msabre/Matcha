package application.servlets;

import adapter.controller.UserController;
import application.services.HttpService;
import application.services.json.JsonService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class ChatDeleteServlet extends HttpServlet {

    private final UserController userController;

    public ChatDeleteServlet(UserController userController) {
        this.userController = userController;
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");

        String body = HttpService.getBody(req);
        int toUsr = Integer.parseInt(Optional.ofNullable(JsonService.getParameter(body, "toUsr")).orElse("-1"));
        if (toUsr < 0) {
            HttpService.putBody(resp, "WRONG 'toUsr' PARAM VALUE");
            return;
        }

        int chatId = Integer.parseInt(Optional.ofNullable(JsonService.getParameter(body, "toUsr")).orElse("-1"));
        if (chatId < 0) {
            HttpService.putBody(resp, "'chatId' VALUE MUST BE MORE 0");
            return;
        }
        if (userController.deleteChat(chatId))
            HttpService.putBody(resp, "SUCCESS");
        else
            HttpService.putBody(resp, "WRONG");
    }
}
