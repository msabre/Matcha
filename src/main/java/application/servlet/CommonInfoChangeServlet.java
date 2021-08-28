package application.servlet;

import adapter.controller.OperationController;
import adapter.controller.UserController;
import application.services.HttpService;
import application.services.MailService;
import application.services.MatchUtils;
import application.services.json.JsonService;
import config.MyProperties;
import domain.entity.User;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static config.MyConfiguration.*;


public class CommonInfoChangeServlet extends HttpServlet {

    private OperationController operationController;
    private UserController userController;

    private final static String changeEmailRqTextForm = "Здравствуйте, %s!\n" +
            "Вы отправили запроса на смену почтового адреса, привязанного к вашей учетной записи Match, перейдите по ссылке для завершения: %s\n" +
            "Эта ссылка будет действовать в течение 24 ч. Если срок действия ссылки истек, отправьте запрос заново\n" +
            "Если вы не отправляли запрос, сообщите нам об этом в ответном письме";

    private final static String ConfirmNewEmailTextForm = "Здравствуйте, %s!\n" +
            "Перейдите по ссылке: %s, чтобы подтвердить данный почтовый адрес в качетсве основного для вашей учетной записи Match\n"+
            "Эта ссылка будет действовать в течение 24 ч. Если срок действия ссылки истек, отправьте запрос заново\n" +
            "Если вы не отправляли запрос, сообщите нам об этом в ответном письме";

    @Override
    public void init() {
        operationController = operationController();
        userController = userController();
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

        String body = HttpService.getBody(req);
        String act = JsonService.getParameter(body,"act");
        if (act == null)
            return;

        User userSession = (User) req.getSession().getAttribute("user");
        if (userSession == null) {
            HttpService.putBody(resp, "WRONG");
            return;
        }

        switch (act) {
            case "emailRqSend":
                sendMail(userSession.getEmail(), getEmailRqSendText(userSession, confirmLinkGenerate(userSession.getId())),
                        "Изменение параметров учетной записи Match");
                break;

            case "emailNewRqSend":
                int linkId = Optional.ofNullable(JsonService.getParameter(body, "linkId")).map(Integer::parseInt).orElse(-1);
                if (!operationController.isCorrectLink(linkId, JsonService.getParameter(body,"token"))) {
                    HttpService.putBody(resp, "WRONG");
                    return;
                }
                String newEmail = JsonService.getParameter(body,"email");
                int id = Optional.ofNullable(JsonService.getParameter(body,"id")).map(Integer::parseInt).orElse(-1);
                User user = userController.findUser(id);
                if (user == null || user.getId() != userSession.getId()) {
                    HttpService.putBody(resp, "WRONG");
                    return;
                }
                if (newEmail == null || newEmail.isEmpty() || !newEmail.contains("@")) {
                    HttpService.putBody(resp, "WRONG EMAIL CONTENT TYPE");
                    return;
                }
                if (user.getEmail().equals(newEmail)) {
                    HttpService.putBody(resp, "THE NEW EMAIL MUST BE DIFFERENT FROM THE EXISTING ONE");
                    return;
                }
                operationController.confirmLink(linkId);
                sendMail(newEmail, getConfirmNewEmailText(user, confirmLinkGenerate(id) + "&email=" + newEmail),
                        "Подтверждение почтового адреса Match");
                break;

            default:
                HttpService.putBody(resp, "UNEXPECTED ACTION PARAMETER");
                break;
        }

        HttpService.putBody(resp, "SUCCESS");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        req.setCharacterEncoding("UTF-8");

        String body = HttpService.getBody(req);
        String filed = JsonService.getParameter(body, "field");
        if (filed == null)
            return;

        int id = Optional.ofNullable(JsonService.getParameter(body, "id")).map(Integer::parseInt).orElse(-1);
        User user = userController.findUser(id);
        User userSession = (User) req.getSession().getAttribute("user");
        if (user == null || userSession == null || user.getId() != userSession.getId()) {
            HttpService.putBody(resp, "WRONG");
            return;
        }

        switch (filed) {
            case "email":
                int linkId = Optional.ofNullable(JsonService.getParameter(body,"linkId")).map(Integer::parseInt).orElse(-1);
                if (!operationController.isCorrectLink(linkId, JsonService.getParameter(body, "token"))) {
                    HttpService.putBody(resp, "WRONG");
                    return;
                }
                operationController.confirmLink(linkId);
                String newEmail = JsonService.getParameter(body, "email");
                userController.updateEmail(id, newEmail);
                user.setEmail(newEmail);
                break;

            case "fio":
                String[] fio = Optional.ofNullable(JsonService.getParameter(body, "fio")).orElse("").split(" ");
                if (fio.length != 3) {
                    HttpService.putBody(resp, "FIO ERROR FORMAT, EXPECTED 3 PARTS");
                    return;
                }
                user.setLastName(fio[0]);
                user.setFirstName(fio[1]);
                user.setMiddleName(fio[2]);
                userController.fioUpdate(id, fio);
                break;

            case "birthDate":
                String birthDate = JsonService.getParameter(body, "birthDate");
                Date date;
                try {
                    date = new SimpleDateFormat("dd.MM.yyyy").parse(birthDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return;
                }
                user.setBirthday(date);
                user.setYearsOld(MatchUtils.getYearsOldFromDate(date));
                userController.birthDateUpdate(id, date, user.getYearsOld());
                break;

            default:
                HttpService.putBody(resp, "UNEXPECTED FIELD PARAMETER");
                return;
        }
        req.getSession().setAttribute("user", user);
        HttpService.putBody(resp, "SUCCESS");
    }

    private String getEmailRqSendText(User user, String link) {
        String fio = String.format("%s %s", user.getFirstName(), user.getMiddleName());
        return String.format(changeEmailRqTextForm, fio, link);
    }

    private String getConfirmNewEmailText(User user, String link) {
        String fio = String.format("%s %s", user.getFirstName(), user.getMiddleName());
        return String.format(ConfirmNewEmailTextForm, fio, link);
    }

    private String confirmLinkGenerate(int userId) {
        String token = MatchUtils.generateRqUid();
        String link = String.format("http://%s/main/emailchange?act=emailChangeConfirm&id=%s&token=%s&linkId=",
                MyProperties.CLIENT_HOST, userId, token);
        return link + operationController.addLink(token);
    }

    private void sendMail(String email, String text, String topic) {
        MailService mailService = new MailService(MyProperties.ADMIN_LOGIN, MyProperties.ADMIN_PASSWORD);
        mailService.setSubject(topic);
        mailService.setText(text);

        try {
            mailService.sendMail(email);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
