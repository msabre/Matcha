package application.servlet;

import adapter.controller.OperationController;
import adapter.controller.UserController;
import application.services.HttpService;
import application.services.MailService;
import application.services.MatchUtils;
import config.MyProperties;
import domain.entity.User;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static config.MyConfiguration.*;


public class EmailChangeServlet extends HttpServlet {

    private OperationController operationController;
    private UserController userController;

    private final static String mailTextForm = "Здравствуйте, %s!\n" +
            "Вы отправили запроса на смену почтового адреса, привязанного к вашей учетной записи Match, перейдите по ссылке для завершения: %s\n" +
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

        String act = req.getParameter("act");
        if (act == null)
            return;

        User user = (User) req.getSession().getAttribute("user");
        if (act.equals("send")) {

            String link = String.format("http://%s/main/emailchange?act=confirm&id=%s&token=%s&linkId=", MyProperties.CLIENT_HOST, user.getId(), MatchUtils.generateRqUid());
            link += operationController.addLink(link);

            MailService mailService = new MailService(MyProperties.ADMIN_LOGIN, MyProperties.ADMIN_PASSWORD);
            mailService.setSubject("Смена почты");
            String fio = String.format("%s %s", user.getFirstName(), user.getMiddleName());
            mailService.setText(String.format(mailTextForm, fio, link));

            try {
                mailService.sendMail(user.getEmail());
            } catch (MessagingException e) {
                e.printStackTrace();
                HttpService.putBody(resp, "WRONG");
                return;
            }

        } else if (act.equals("confirm")) {
            int linkId = Optional.ofNullable(req.getParameter("linkId")).map(Integer::parseInt).orElse(-1);

            if (!operationController.isCorrectLink(linkId, HttpService.getUrl(req))) {
                HttpService.putBody(resp, "WRONG");
                return;
            }
            operationController.confirmLink(linkId);
        }

        HttpService.putBody(resp, "SUCCESS");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        req.setCharacterEncoding("UTF-8");

        int id = Optional.ofNullable(req.getParameter("id")).map(Integer::parseInt).orElse(-1);
        String newEmail = req.getParameter("email");

        if (newEmail == null || newEmail.isEmpty() || !newEmail.contains("@")) {
            HttpService.putBody(resp, "WRONG");
            return;
        }

        User user = userController.findUser(id);
        if (user == null || user.getEmail().equals(newEmail)) {
            HttpService.putBody(resp, "WRONG");
            return;
        }
        userController.updateEmail(id, newEmail);
    }
}
