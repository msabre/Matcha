package application.servlets;

import adapter.controller.UserController;
import application.services.HttpService;
import application.services.json.JsonService;
import domain.entity.User;
import domain.entity.model.OnlineStatus;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import static config.MyConfiguration.userController;

public class GetOnlineStatusServlet extends HttpServlet {

    private UserController userController;

    @Override
    public void init() {
        userController = userController();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = HttpService.getBody(req);
            Integer[] ids = (Integer[]) JsonService.parseArray(body, "ids", Integer[].class);
            List<OnlineStatus> statusList = userController.getUserStatusesByIds(ids);

            String location = ((User) req.getSession().getAttribute("user")).getLocation();
            userController.leadLastActionToLocationTimeStatus(statusList, location);
            HttpService.putBody(resp, JsonService.getJsonArray(statusList));

        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpService.putBody(resp, "WRONG");
    }
}
