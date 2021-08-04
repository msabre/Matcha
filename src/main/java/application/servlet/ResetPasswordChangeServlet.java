package application.servlet;


import adapter.controller.OperationController;
import adapter.controller.UserController;

import application.services.HttpService;
import application.services.json.JsonService;
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
import static java.util.Objects.isNull;


public class ResetPasswordChangeServlet extends HttpServlet {

    private UserController userController;
    private PasswordEncoder passwordEncoder;
    private OperationController operationController;


    @Override
    public void init() {
        userController = userController();
        passwordEncoder = passwordEncoder();
        operationController = operationController();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setCharacterEncoding("UTF-8");

        Integer linkId = Optional.ofNullable(req.getParameter("linkId")).map(Integer::parseInt).orElse(-1);

        if (!operationController.checkLink(linkId, HttpService.getUrl(req))) {
            HttpService.putBody(resp, "ERROR LINK");
            return;
        }
        operationController.confirmLink(linkId);

        HttpService.putBody(resp, "SUCCESS");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String body = HttpService.getBody(req);
        Integer id = Optional.ofNullable(JsonService.getParameter(body, "id")).map(Integer::parseInt).orElse(-1);
        String password = JsonService.getParameter(body,"password");

        User user = userController.findUser(id);
        if (isNull(user) || newPasswordIsNotDifferent(password, user)) {
            HttpService.putBody(resp, "NEW PASS NOT DIFFERENT BY OLD");
            return ;
        }

        String hashPassword = passwordEncoder.encrypt(password, null);

        if (userController.passwordUpdate(id, hashPassword))
            HttpService.putBody(resp, "SUCCESS");
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
