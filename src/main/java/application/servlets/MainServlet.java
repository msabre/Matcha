package application.servlets;

import adapter.controller.UserController;
import application.services.HttpService;
import application.services.json.JsonService;

import config.MyProperties;
import domain.entity.FilterParams;
import domain.entity.User;
import domain.entity.model.UserInteraction;
import domain.entity.model.types.Action;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static config.MyConfiguration.userController;
import static java.util.Objects.isNull;


public class MainServlet extends HttpServlet {
    private final String GET_LIST = "getList";
    private final String GET_ACTIONS = "getActions";

    private UserController userController;

    @Override
    public void init() {
        userController = userController();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setCharacterEncoding("UTF-8");

        String act = req.getParameter("act");
        if (isNull(act))
            return;

        User user = (User) req.getSession().getAttribute("user");
        switch (act) {
            case GET_LIST:
                List<User> usersList = userController.getRecommendUsersList(user, MyProperties.USERS_LIST_SIZE);
                usersList.forEach(u -> userController.uploadPhotosContent(u.getCard().getPhotos()));
                HttpService.putBody(resp, JsonService.getJsonArray(usersList));
                break;
            case GET_ACTIONS:
                Action action = Action.valueOf(Optional.ofNullable(req.getParameter("action")).orElse(Action.MATCH.getValue()));
                List<UserInteraction> userInteractions;

                int afterId = Integer.parseInt(Optional.ofNullable(req.getParameter("after")).orElse("-1"));
                if (afterId >= 0)
                    userInteractions = userController.getUserActionListWithSizeAfterSpecificId(action, user.getId(), afterId, MyProperties.USER_MATCH_SIZE);
                else
                    userInteractions = userController.getUserActionListWithSize(action, user.getId(), MyProperties.USER_MATCH_SIZE);
                userInteractions.forEach(m -> userController.uploadPhotosContent(Collections.singleton(m.getIcon())));
                HttpService.putBody(resp, JsonService.getJsonArray(userInteractions));
                break;
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");

        String filters = HttpService.getBody(req);
        User user = (User) req.getSession().getAttribute("user");

        if (!filters.isEmpty()) {
            FilterParams filterParams = (FilterParams) JsonService.getObject(FilterParams.class, filters);
            if (filterParams.getRating() == null
                    || filterParams.getCommonTagsCount() == null
                    || filterParams.getAgeBy() == null
                    || filterParams.getAgeTo() == null
                    || filterParams.getLocation() == null) {
                HttpService.putBody(resp, "NOT ALL REQUIRED FIELDS ARE FILLED IN");
                return;
            }
            filterParams.setId(user.getFilter().getId());
            userController.filterUpdate(filterParams);
            user.setFilter(filterParams);
            req.getSession().setAttribute("user", user);
            HttpService.putBody(resp, "SUCCESS");
        }
    }
}
