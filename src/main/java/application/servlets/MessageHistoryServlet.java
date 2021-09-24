package application.servlets;

import adapter.controller.MessageController;
import application.services.HttpService;
import application.services.json.JsonService;
import domain.entity.Message;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class MessageHistoryServlet extends HttpServlet {
    private MessageController messageController;

    public MessageHistoryServlet() {
//         this.messageController = messageController();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String act = req.getParameter("act");
        int toId = Integer.parseInt(req.getParameter("id"));
        switch (act) {
            case "clearAll":
                messageController.clearAll(toId);
                break;
            case "getAll":
                List<Message> list = messageController.getAll(toId);
                String answer = JsonService.getJsonArray(list);
                HttpService.putBody(resp, answer);
                break;
        }
    }
}
