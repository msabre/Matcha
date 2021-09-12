package application.servlets;

import javax.servlet.http.*;
import java.io.IOException;

public class WelcomeServlet extends HttpServlet {

    @Override
    public void init() {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");

        resp.sendRedirect(resp.encodeRedirectURL("main"));
    }

}
