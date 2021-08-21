package application.servlet;


import adapter.controller.OperationController;
import adapter.controller.UserController;

import application.services.HttpService;
import application.services.MailService;
import application.services.json.JsonService;
import config.MyProperties;
import domain.entity.User;
import usecase.port.PasswordEncoder;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static config.MyConfiguration.operationController;
import static config.MyConfiguration.passwordEncoder;
import static config.MyConfiguration.userController;
import static java.util.Objects.isNull;


public class ResetPasswordSendServlet extends HttpServlet {

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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        String email = JsonService.getParameter(HttpService.getBody(req), "email");
        User user = userController.findUser(email);

        if (isNull(user)) {
            HttpService.putBody(resp, "WRONG");
            return;
        }

        String token = passwordEncoder.getToken(user.getEmail() + user.getFirstName() + user.getId());

        String link = String.format("http://%s/resetpasschange?id=%s&passtoken=%s&linkId=", MyProperties.CLIENT_HOST, user.getId(), token);
        link += operationController.addLink(link);

        String fio = String.format("%s %s", user.getFirstName(), user.getMiddleName());
        String text = String.format("Здравствуйте, %s!\nДля сброса пароля перейдите по ссылке: %s", fio, link);

        MailService mailService = new MailService(MyProperties.ADMIN_LOGIN, MyProperties.ADMIN_PASSWORD);
        mailService.setSubject("Сброс пароля");
        mailService.setText(text);

        try {
            mailService.sendMail(email);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        HttpService.putBody(resp, "SUCCESS");
    }
}
