package application.filters;

import adapter.controller.JwtController;
import application.services.HttpService;
import application.services.LocationService;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import domain.entity.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static config.MyConfiguration.jwtController;

public class JwtFilter implements Filter {

    private JwtController jwtController;

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

        if (jwtController.checkJwt(req, resp) < 0) {
            HttpService.putBody((HttpServletResponse) resp, "Error JWT");
            return;
        }
        checkUserLocation((HttpServletRequest) req);

        chain.doFilter(req, resp);
    }

    private void checkUserLocation(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user.getLocation() != null)
            return ;

        String ip = HttpService.getClientIpAddress(request);
        try {
            String location = LocationService.getPosition("188.255.7.63");
            user.setLocation(location);

        } catch (IOException | GeoIp2Exception e) {
            e.printStackTrace();
        }
    }

    public void init(FilterConfig config) {
        jwtController = jwtController();
    }

}
