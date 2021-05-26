package application.Servlet;


import adapter.controller.OperationController;
import adapter.controller.UserController;

import application.services.HttpService;
import domain.entity.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static config.MyConfiguration.operationController;
import static config.MyConfiguration.userController;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;


public class ConfirmAccountServlet extends HttpServlet {

    private UserController userController;
    private OperationController operationController;

    @Override
    public void init() throws ServletException {
        userController = userController();
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

        int id = Integer.parseInt(req.getParameter("id"));

        User webBean = userController.findUser(id);

        if (isNull(webBean)) {
            HttpService.putBody(resp, "WRONG");
            return ;
        }

        String confirmToken = req.getParameter("conf");

        if (nonNull(confirmToken) && confirmToken.equals(webBean.getTokenConfirm())) {
            webBean.setConfirm(true);

            userController.confirmUser(id);
            operationController.confirmLink(linkId);

            HttpService.putBody(resp, "OK");
        }
        else
            HttpService.putBody(resp, "WRONG");
    }

    private String getUrl(HttpServletRequest request) {
        return request.getScheme() + "://" +
                request.getServerName() +
                ("http".equals(request.getScheme()) && request.getServerPort() == 80
                        || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) +
                request.getRequestURI() +
                (request.getQueryString() != null ? "?" + request.getQueryString() : "");
    }
}
