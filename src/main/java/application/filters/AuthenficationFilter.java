package application.filters;



import domain.entity.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.Objects.nonNull;

public class AuthenficationFilter implements Filter {

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

        User userWeb = (User) ((HttpServletRequest) req).getSession().getAttribute("user");
        if (nonNull(userWeb) && userWeb.isAuthorized()) {
            ((HttpServletResponse) resp).sendRedirect("/main");
            return ;
        }

        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) {

    }

}
