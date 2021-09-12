package application.servlets;


import adapter.controller.*;


import application.services.HttpService;
import application.services.LocationService;
import application.services.MailService;
import application.services.MatchUtils;
import application.services.json.JsonService;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import config.MyProperties;
import domain.entity.User;
import domain.entity.UserCard;import usecase.port.PasswordEncoder;

import javax.mail.MessagingException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


import static config.MyConfiguration.*;


public class RegistrationServlet extends HttpServlet {

    private UserController userController;
    private PasswordEncoder passwordEncoder;
    private OperationController operationController;

    private final static String mailTextForm = "Здравствуйте, уважаемый %s!\n" +
            "Подтвердите свой адрес электронной почты, чтобы завершить создание своей учетной записи Match по ссылке: %s\n" +
            "Эта ссылка будет действовать в течение 24 ч. Если срок действия ссылки истек, попробуйте запросить новое " +
            "электронное письмо для подтверждения..";

    @Override
    public void init() {
        userController = userController();
        passwordEncoder = passwordEncoder();
        operationController = operationController();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        req.setCharacterEncoding("UTF-8");

        String json = HttpService.getBody(req);
        User user = (User) JsonService.getObject(User.class, json);

        user.setYearsOld(MatchUtils.getYearsOldFromDate(user.getBirthday()));

        String hashPassword = passwordEncoder.encrypt(user.getPassword(), null);
        user.setPassword(hashPassword);
        user.setConfirm(false);
        // user.setTokenConfirm(null);
         user.setTokenConfirm(passwordEncoder.getToken(MatchUtils.generateRqUid()));

        String location = null;
        try {
            location = LocationService.getPosition("188.255.7.63");
        } catch (GeoIp2Exception e) {
            e.printStackTrace();
        }

        user.setLocation(location);

        UserCard userCard = (UserCard) JsonService.getObjectByExposeFields(UserCard.class, json);
        user.setCard(userCard);

        int userId = userController.createUser(user);
        if (userId < 0)
            HttpService.putBody(resp, "WRONG");

        user.setId(userId);
        userCard.setUserId(userId);

        sendConfirmMail(user);
        HttpService.putBody(resp, "SUCCESS");
    }

    private void sendConfirmMail (User user) {
        String link = String.format("http://%s/confirmAccount?id=%s&conf=%s&linkId=", MyProperties.CLIENT_HOST, user.getId(), user.getTokenConfirm());
        link +=  operationController.addLink(link);

        MailService mailService = new MailService(MyProperties.ADMIN_LOGIN, MyProperties.ADMIN_PASSWORD);

        mailService.setSubject("Подтверждение регистрации");
        String fio = String.format("%s %s", user.getFirstName(), user.getMiddleName());
        mailService.setText(String.format(mailTextForm, fio, link));

        try {
            mailService.sendMail(user.getEmail());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
