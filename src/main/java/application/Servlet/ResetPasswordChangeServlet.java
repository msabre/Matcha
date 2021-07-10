package application.Servlet;


import adapter.controller.OperationController;
import adapter.controller.UserController;

import application.services.HttpService;
import config.MyProperties;
import domain.entity.User;
import usecase.port.PasswordEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static config.MyConfiguration.operationController;
import static config.MyConfiguration.passwordEncoder;
import static config.MyConfiguration.userController;


public class ResetPasswordChangeServlet extends HttpServlet {

    private UserController userController;
    private PasswordEncoder passwordEncoder;
    private OperationController operationController;


    @Override
    public void init() throws ServletException {
        userController = userController();
        passwordEncoder = passwordEncoder();
        operationController = operationController();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");

        Integer linkId = Optional.ofNullable(req.getParameter("linkId")).map(Integer::parseInt).orElse(-1);

        if (!operationController.checkLink(linkId, getUrl(req))) {
            HttpService.putBody(resp, "ERROR LINK");
            return;
        }

        Integer id = Optional.ofNullable(req.getParameter("id")).map(Integer::parseInt).orElse(-1);
        req.getSession().setAttribute("id", id);
        req.getSession().setAttribute("linkId", linkId);

        HttpService.putBody(resp, "SUCCESS");
    }

    private String getUrl(HttpServletRequest request) {
        return request.getScheme() + "://" +
                request.getServerName() +
                ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) +
                request.getRequestURI() +
                (request.getQueryString() != null ? "?" + request.getQueryString() : "");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        Integer id = (Integer) req.getSession().getAttribute("id");
        String password = req.getParameter("password");

        User user = userController.findUser(id);
        if (newPasswordIsNotDifferent(password, user)) {
            HttpService.putBody(resp, "NEW PASS NOT DIFFERENT BY OLD");
            return ;
        }

        String hashPassword = passwordEncoder.encrypt(password, null);
        boolean passHasUpdate = userController.passwordUpdate(id, hashPassword);

        if (passHasUpdate) {
            int linkId = (Integer) req.getSession().getAttribute("linkId");
            operationController.confirmLink(linkId);

            HttpService.putBody(resp, "SUCCESS");
        }
        else
            HttpService.putBody(resp, "WRONG");
    }

    private boolean newPasswordIsNotDifferent(String newPassword, User user) {
        String oldPasswordHash = user.getPassword();
        String salt = oldPasswordHash.substring(oldPasswordHash.length() - (MyProperties.SALT_BYTES_COUNT + 8));

        String newPasswordHash = passwordEncoder.encrypt(newPassword, salt);

        return oldPasswordHash.equals(newPasswordHash);
    }
}
