package application.servlets;

import adapter.controller.JwtController;
import adapter.controller.UserController;
import application.services.HttpService;
import application.services.json.JsonService;
import config.MyConfiguration;
import config.MyProperties;
import domain.entity.JsonWebToken;
import domain.entity.User;
import domain.entity.model.types.JwtType;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WebSocketGetAccessServlet extends HttpServlet {

    private JwtController jwtController;

    @Override
    public void init() {
        jwtController = MyConfiguration.jwtController();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setCharacterEncoding("UTF-8");

        User user = (User) req.getSession().getAttribute("user");

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());

        jwtController.removeTokenByUserId(user.getId(), JwtType.WEBSOCKET);
        HttpService.putBody(resp, JsonService.getJsonChat(jwtController.getAccessToken(user, claims)));
    }

}
