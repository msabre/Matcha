package application.servlets;

import adapter.controller.UserController;
import application.services.HttpService;
import application.services.json.JsonService;

import config.MyProperties;
import domain.entity.FilterParams;
import domain.entity.User;
import domain.entity.model.ActionHistory;
import domain.entity.model.types.Action;
import domain.entity.model.types.CityType;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static config.MyConfiguration.userController;
import static java.util.Objects.isNull;


public class MainServlet extends HttpServlet {
    private final String GET_USERS = "getList";
    private final String GET_ACTIONS_HISTORY = "getActions";

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
            case GET_USERS:
                List<User> usersList = userController.getRecommendUsersList(user, MyProperties.USERS_LIST_SIZE);
                usersList.forEach(u -> userController.uploadPhotosContent(u.getCard().getPhotos()));
                HttpService.putBody(resp, JsonService.getJsonArrayWithExpose(usersList));
                break;
            case GET_ACTIONS_HISTORY:
                Action action = Action.valueOf(Optional.ofNullable(req.getParameter("action")).orElse(Action.MATCH.getValue()));
                int afterId = Integer.parseInt(Optional.ofNullable(req.getParameter("after")).orElse("-1"));

                List<ActionHistory> history;
                if (action == Action.MATCH) {
                    if (afterId >= 0)
                        history = userController.getFromActionsAfterId(action, user.getId(), afterId, MyProperties.USER_MATCH_SIZE);
                    else
                        history = userController.getFromActions(action, user.getId(), MyProperties.USER_MATCH_SIZE);
                } 
                else {
                    if (afterId >= 0)
                        history = userController.getToActionsAfterId(action, user.getId(), afterId, MyProperties.USER_MATCH_SIZE);
                    else
                        history = userController.getToActions(action, user.getId(), MyProperties.USER_MATCH_SIZE);
                }

                history.forEach(m -> {
                    if (m.getIcon() != null)
                        userController.uploadMainPhotoContent(m.getIcon());
                });
                HttpService.putBody(resp, JsonService.getJsonArrayWithExpose(history));
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
