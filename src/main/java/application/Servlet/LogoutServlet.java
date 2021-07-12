package application.Servlet;

import adapter.controller.JwtController;
import config.MyConfiguration;
import domain.entity.User;

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

        User user = (User) req.getSession().getAttribute("user");
        jwtController.removeTokenByUserId(user.getId());
        jwtController.deleteJwtCookies(req, resp);
    }

    @Override
    public void init() throws ServletException {
        jwtController = MyConfiguration.jwtController();
    }
}
