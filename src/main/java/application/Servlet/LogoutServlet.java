package application.Servlet;

import adapter.controller.JwtController;
import application.services.HttpService;
import config.MyConfiguration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class LogoutServlet extends HttpServlet {

    private JwtController jwtController;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        int id = Integer.parseInt(req.getParameter("id"));

        if (id < 0) {
            jwtController.removeTokenByUserId(id);
        } else {
            HttpService.putBody(resp, "WRONG");
        }
    }

    @Override
    public void init() throws ServletException {
        jwtController = MyConfiguration.jwtController();
    }
}
