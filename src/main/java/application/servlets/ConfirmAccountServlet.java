package application.servlets;


import adapter.controller.OperationController;
import adapter.controller.UserController;

import application.services.HttpService;
import domain.entity.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static config.MyConfiguration.operationController;
import static config.MyConfiguration.userController;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;


public class ConfirmAccountServlet extends HttpServlet {

    private UserController userController;
    private OperationController operationController;

    @Override
    public void init() {
        userController = userController();
        operationController = operationController();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setCharacterEncoding("UTF-8");

        Integer linkId = Optional.ofNullable(req.getParameter("linkId")).map(Integer::parseInt).orElse(-1);
        String confirmToken = req.getParameter("conf");
        if (!operationController.isCorrectLink(linkId, confirmToken)) {
            HttpService.putBody(resp, "ERROR LINK");
            return;
        }

        int id = Integer.parseInt(req.getParameter("id"));

        User user = userController.findUser(id);
        if (isNull(user)) {
            HttpService.putBody(resp, "WRONG");
            return ;
        }

        if (confirmToken.equals(user.getTokenConfirm())) {
            user.setConfirm(true);

            userController.confirmUser(id);
            operationController.confirmLink(linkId);

            HttpService.putBody(resp, "SUCCESS");
        }
        else
            HttpService.putBody(resp, "WRONG");
    }
}
