package application.servlet;


import adapter.controller.UserController;
import application.services.HttpService;
import application.services.json.JsonService;

import domain.entity.FilterParams;
import domain.entity.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static config.MyConfiguration.userController;
import static java.util.Objects.isNull;


public class MainServlet extends HttpServlet {

    private UserController userController;

    @Override
    public void init() {
        userController = userController();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");

        String act = req.getParameter("act");
        if (isNull(act) || !act.equals("getList"))
            return;

        String filters = HttpService.getBody(req);
        FilterParams filterParams = (FilterParams) JsonService.getObjectByExposeFields(FilterParams.class, filters);

        User user = (User) req.getSession().getAttribute("user");
        if (filterParams != null)
            user.setFilter(filterParams);

        List<User> usersList = userController.getRecommendUsersList(user);

        HttpService.putBody(resp, JsonService.getJsonArray(usersList));
    }
}
