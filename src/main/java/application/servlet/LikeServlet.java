package application.servlet;

import adapter.controller.UserController;
import config.MyConfiguration;
import domain.entity.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class LikeServlet extends HttpServlet {
    private UserController userController;

    @Override
    public void init() {
        userController = MyConfiguration.userController();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");

        int id = Integer.parseInt(req.getParameter("id"));
        String act = req.getParameter("act");

        if (act == null || user == null)
            return;

        if (act.equals("match")) {
            userController.match(user.getId(), id);
        }
        if (act.equals("like")) {
            userController.like(user.getId(), id);
        }
        else if (act.equals("dislike"))
            userController.deleteLike(user.getId(), id);
    }
}
