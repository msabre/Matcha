package application.servlets;


import adapter.controller.OperationController;
import adapter.controller.UserController;

import application.services.HttpService;
import application.services.MailService;
import application.services.MatchUtils;
import application.services.json.JsonService;
import config.MyProperties;
import domain.entity.User;
import usecase.port.PasswordEncoder;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static config.MyConfiguration.operationController;
import static config.MyConfiguration.passwordEncoder;
import static config.MyConfiguration.userController;
import static java.util.Objects.isNull;


public class PasswordChangeServlet extends HttpServlet {

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        int linkId = Optional.ofNullable(req.getParameter("linkId")).map(Integer::parseInt).orElse(-1);
        if (!operationController.isCorrectLink(linkId, req.getParameter("token"))) {
            HttpService.putBody(resp, "WRONG");
            return;
        }
        HttpService.putBody(resp, "SUCCESS");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        String email = JsonService.getParameter(HttpService.getBody(req), "email");
        User user = userController.findUser(email);

        if (isNull(user)) {
            HttpService.putBody(resp, "WRONG");
            return;
        }
        String token = passwordEncoder.getToken(MatchUtils.generateRqUid());
        String link = String.format("%s://%s/passchange?id=%s&token=%s&linkId=", MyProperties.HTTP_PROTOCOL, MyProperties.CLIENT_HOST, user.getId(), token);
        link += operationController.addLink(token);
        String fio = String.format("%s %s", user.getFirstName(), user.getMiddleName());
        String text = String.format("Здравствуйте, %s!\nДля сброса пароля перейдите по ссылке: %s", fio, link);
        MailService mailService = new MailService(MyProperties.ADMIN_LOGIN, MyProperties.ADMIN_PASSWORD);
        mailService.setSubject("Сброс пароля");
        mailService.setText(text);
        try {
            mailService.sendMail(email);
        } catch (MessagingException e) {
            HttpService.putBody(resp, "EMAIL SEND ERROR, TRY LATER");
            e.printStackTrace();
        }
        HttpService.putBody(resp, "SUCCESS");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        String body = HttpService.getBody(req);
        int linkId = Optional.ofNullable(JsonService.getParameter(body,"linkId")).map(Integer::parseInt).orElse(-1);
        if (!operationController.isCorrectLink(linkId, JsonService.getParameter(body, "token"))) {
            HttpService.putBody(resp, "WRONG LINK");
            return;
        }
        operationController.confirmLink(linkId);

        Integer id = Optional.ofNullable(JsonService.getParameter(body, "id")).map(Integer::parseInt).orElse(-1);
        String password = JsonService.getParameter(body,"password");

        User user = userController.findUser(id);
        if (user == null) {
            HttpService.putBody(resp, "WRONG");
            return ;
        }
        if (newPasswordIsNotDifferent(password, user)) {
            HttpService.putBody(resp, "NEW PASS NOT DIFFERENT BY OLD");
            return ;
        }

        String hashPassword = passwordEncoder.encrypt(password, null);
        if (userController.passwordUpdate(id, hashPassword)) {
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
