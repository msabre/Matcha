package application.servlet;

import adapter.controller.JwtController;
import config.MyConfiguration;
import domain.entity.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class LogoutServlet extends HttpServlet {

    private JwtController jwtController;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        // Если юзера нет значит юзер не в сети
        User user = (User) req.getSession().getAttribute("user");
        if (user == null)
            return ;
        jwtController.removeTokenByUserId(user.getId());
        jwtController.deleteJwtCookies(req, resp);
    }

    @Override
    public void init() {
        jwtController = MyConfiguration.jwtController();
    }
}
