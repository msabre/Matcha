package application.Servlet;


import adapter.controller.JwtController;
import adapter.controller.UserController;
import application.services.HttpService;
import application.services.json.JsonService;
import com.google.gson.*;
import domain.entity.User;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import static config.MyConfiguration.jwtController;
import static config.MyConfiguration.userController;
import static java.util.Objects.isNull;


public class LoginServlet extends HttpServlet {

    private UserController userController;
    private JwtController jwtController;

    @Override
    public void init() {
        userController = userController();
        jwtController = jwtController();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String body = HttpService.getBody(req);
        JsonObject object = JsonParser.parseString(body).getAsJsonObject();

        String email = object.get("email").getAsString();
        String password = object.get("password").getAsString();

        User user = userController.loginUser(email, password);
        if (isNull(user)) {
            HttpService.putBody(resp, "INVALID LOGIN OR PASSWORD");
            return;
        }

        if (user.isConfirm()) {

            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());

            if (jwtController.issueTokensPair(req, resp, user, claims) != null) {
                HttpService.putBody(resp, JsonService.getJson(user));
                return;
            }
        }

        HttpService.putBody(resp, "WRONG");
    }

}
