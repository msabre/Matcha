package application.servlet;


import adapter.controller.*;


import application.services.HttpService;
import application.services.LocationService;
import application.services.MailService;
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
import java.time.*;
import java.util.Date;


import static config.MyConfiguration.*;


public class RegistrationServlet extends HttpServlet {

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

        String json = HttpService.getBody(req);
        User user = (User) JsonService.getObject(User.class, json);

        user.setYearsOld(getYearsOld(user.getBirthday()));

        String hashPassword = passwordEncoder.encrypt(user.getPassword(), null);
        user.setPassword(hashPassword);
        user.setConfirm(false);
        user.setTokenConfirm(null);
        // userWeb.setConfirmToken(passwordEncoder.getToken(userWeb.getName() + userWeb.getEmail() + userWeb.getCountry()));

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

        req.getSession().setAttribute("email", user.getEmail());

        // sendConfirmMail(userWeb);
        HttpService.putBody(resp, "SUCCESS");
    }

    private int getYearsOld(Date birthDay) {
        LocalDate birthDate = birthDay.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate now = new Date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        return Period.between(birthDate, now).getYears();
    }

    private void sendConfirmMail (User user) {
        String link = "http://localhost:8080/confirmAccount?id=" + user.getId() + "&conf=" + user.getTokenConfirm() + "&linkId=";
        link +=  operationController.addLink(link);

        MailService mailService = new MailService(MyProperties.ADMIN_LOGIN, MyProperties.ADMIN_PASSWORD);

        mailService.setSubject("Test");
        mailService.setText("здравствуйте, " + user.getFirstName() + " " + user.getMiddleName() + "!\n" +
                "Подтвердите свой адрес электронной почты, чтобы завершить создание своей учетной записи Match по ссылке:" +
                link + "\n" +
                "Эта ссылка будет действовать в течение 24 ч. Если срок действия ссылки истек, попробуйте запросить новое " +
                "электронное письмо для подтверждения..");

        try {
            mailService.sendMail(user.getEmail());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
