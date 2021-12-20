package application.servlets;

import adapter.controller.JwtController;
import adapter.controller.UserController;
import adapter.port.model.LocationTimeZoneUTC;
import config.MyConfiguration;
import domain.entity.User;
import domain.entity.model.OnlineStatus;
import domain.entity.model.types.JwtType;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneId;


public class LogoutServlet extends HttpServlet {

    private JwtController jwtController;
    private UserController userController;
    private LocationTimeZoneUTC locationTimeZoneUTC;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        // Если юзера нет значит юзер не в сети
        User user = (User) req.getSession().getAttribute("user");
        if (user == null)
            return ;

        jwtController.removeTokenByUserId(user.getId(), JwtType.HTTP);
        jwtController.deleteJwtCookies(req, resp);
        userController.updateStatus(user.getId(), locationTimeZoneUTC.getZoneIdByCity(user.getLocation()), OnlineStatus.Status.OFFLINE);
        req.getSession().invalidate();
    }

    @Override
    public void init() {
        jwtController = MyConfiguration.jwtController();
        userController = MyConfiguration.userController();
        locationTimeZoneUTC = MyConfiguration.locationTimeZoneUTC();
    }
}
