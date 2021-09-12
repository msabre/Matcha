package application.servlets;


import adapter.controller.UserController;
import application.services.HttpService;
import application.services.json.JsonService;

import domain.entity.FilterParams;
import domain.entity.User;

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");

        String act = req.getParameter("act");
        if (isNull(act))
            return;

        switch (act) {
            case "getList":
                String filters = HttpService.getBody(req);
                User user = (User) req.getSession().getAttribute("user");
                if (!filters.isEmpty()) {
                    FilterParams filterParams = (FilterParams) JsonService.getObject(FilterParams.class, filters);
                    filterParams.setId(user.getFilter().getId());
                    if (isNull(filterParams.getLocation()))
                        filterParams.setLocation(user.getLocation());

                    userController.filterUpdate(filterParams);
                    user.setFilter(filterParams);
                    req.getSession().setAttribute("user", user);
                }
                List<User> usersList = userController.getRecommendUsersList(user);
                HttpService.putBody(resp, JsonService.getJsonArray(usersList));
                break;
            case "getContacts":
                break;
        }
    }
}
