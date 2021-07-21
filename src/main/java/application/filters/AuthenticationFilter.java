package application.filters;



import adapter.controller.JwtController;
import config.MyConfiguration;
import javafx.util.Pair;

import javax.servlet.*;
import java.io.IOException;

public class AuthenticationFilter implements Filter {
    private JwtController jwtController;

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

        Pair<String, String> rsToken = jwtController.getRsToken(req);
        if (rsToken != null) {
            return ;
        }

        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) {
        jwtController = MyConfiguration.jwtController();
    }

}
