package application.filters;

import adapter.controller.JwtController;
import application.services.HttpService;
import application.services.LocationService;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import domain.entity.JsonWebToken;
import domain.entity.User;
import io.jsonwebtoken.ExpiredJwtException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static config.MyConfiguration.jwtController;
import static java.util.Objects.isNull;

public class JwtFilter implements Filter {

    private JwtController jwtController;

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

        JsonWebToken jws = (JsonWebToken) ((HttpServletRequest)req).getSession().getAttribute("jws");

        if (isNull(jws)) {

            if (jwtController.refreshToken(req, resp)) {
                checkUserLocation((HttpServletRequest) req);
                chain.doFilter(req, resp);
                return ;
            }
            else {
                req.getRequestDispatcher("views/index.jsp").forward(req, resp);
                return ;
            }
        }

        try {
            jwtController.verifyJWT(jws.getToken(), jws.getUserFingerprint());

        } catch (ExpiredJwtException expiredJwtException) {
            if (!jwtController.refreshToken(req, resp)) {
                checkUserLocation((HttpServletRequest) req);
                req.getRequestDispatcher("views/index.jsp").forward(req, resp);
                return;
            }

        } catch (Exception e) {
            req.getRequestDispatcher("views/index.jsp").forward(req, resp);
            return ;

        }

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

            User userWeb2 = (User) request.getSession().getAttribute("user");
            System.out.println(userWeb2);
        } catch (IOException | GeoIp2Exception e) {
            e.printStackTrace();
        }
    }

    public void init(FilterConfig config) {
        jwtController = jwtController();
    }

}
