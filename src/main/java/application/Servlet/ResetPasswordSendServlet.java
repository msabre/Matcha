package application.Servlet;


import adapter.controller.OperationController;
import adapter.controller.UserController;

import application.services.HttpService;
import application.services.MailService;
import com.google.gson.JsonParser;
import config.MyProperties;
import domain.entity.User;
import usecase.port.PasswordEncoder;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
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
    public void init() throws ServletException {
        userController = userController();
        passwordEncoder = passwordEncoder();
        operationController = operationController();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String body = HttpService.getBody(req);
        String email = JsonParser.parseString(body).getAsJsonObject().get("email").getAsString();
        User user = userController.findUser(email);

        if (isNull(user)) {
            HttpService.putBody(resp, "WEONG");
            return;
        }

        String token = passwordEncoder.getToken(user.getEmail() + user.getFirstName() + user.getId());

        String link = "http://localhost:8080/resetpasschange?id=" + user.getId() + "&passtoken=" + token + "&linkId=";
        link += operationController.addLink(link);

        MailService mailService = new MailService(MyProperties.ADMIN_LOGIN, MyProperties.ADMIN_PASSWORD);
        mailService.setSubject("Сброс пароля");
        mailService.setText("здравствуйте, " + user.getFirstName() + " " + user.getMiddleName() + "!\n" +
                "Для сброса пароля перейдите по ссылке: " + link);

        try {
            mailService.sendMail(email);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        HttpService.putBody(resp, "SUCCESS");
    }
}
