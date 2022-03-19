package application.filters;

import adapter.controller.JwtController;
import adapter.controller.UserController;
import adapter.port.model.LocationTimeZoneUTC;
import application.services.HttpService;
import application.services.LocationService;
import application.services.MatchUtils;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import domain.entity.User;
import domain.entity.model.OnlineStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static config.MyConfiguration.*;

public class JwtFilter implements Filter {

    private JwtController jwtController;
    private UserController userController;
    private LocationTimeZoneUTC locationTimeZoneUTC;

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

        boolean success = jwtController.checkJwt(req, resp);
        User user = (User) ((HttpServletRequest) req).getSession().getAttribute("user");

        if (!success) {
            userController.updateStatus(user.getId(), locationTimeZoneUTC.getZoneIdByCity(user.getLocation()), OnlineStatus.Status.OFFLINE);
            HttpService.putBody((HttpServletResponse) resp, "Error JWT");
            return;
        }

        userController.updateStatus(user.getId(), locationTimeZoneUTC.getZoneIdByCity(user.getLocation()), OnlineStatus.Status.ONLINE);
        checkUserLocation((HttpServletRequest) req, user);
        chain.doFilter(req, resp);
    }

    private void checkUserLocation(HttpServletRequest request, User user) {
        if (user.getLocation() != null)
            return ;

        String ip = HttpService.getClientIpAddress(request);
        try {
            String location = MatchUtils.getCityNameByGeoLiteCityName(LocationService.getPosition(ip));
            user.setLocation(location);

        } catch (IOException | GeoIp2Exception e) {
            e.printStackTrace();
        }
    }

    public void init(FilterConfig config) {
        jwtController = jwtController();
        userController = userController();
        locationTimeZoneUTC = locationTimeZoneUTC();
    }

}
