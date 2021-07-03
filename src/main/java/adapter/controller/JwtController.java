package adapter.controller;

import config.MyConfiguration;
import domain.entity.JsonWebToken;
import domain.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import javafx.util.Pair;
import usecase.CreateTokenJWS;
import usecase.GetTokenId;
import usecase.RemoveTokenJWS;
import usecase.VerifyTokenJWS;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static application.services.HttpService.getCookie;
import static java.util.Objects.isNull;

public class JwtController {
    private static JwtController instance;

    private UserController userController;

    private CreateTokenJWS createToken;
    private VerifyTokenJWS verifyTokenJWS;
    private GetTokenId getTokenId;
    private RemoveTokenJWS removeTokenJWS;

    private JwtController() {
    }

    public static JwtController getController() {
        if (instance == null) {
            instance = new JwtController();

            instance.createToken = MyConfiguration.createTokenJWS();
            instance.verifyTokenJWS = MyConfiguration.verifyTokenJWS();
            instance.getTokenId = MyConfiguration.refreshTokenJWS();
            instance.removeTokenJWS = MyConfiguration.removeTokenJWS();
            instance.userController = MyConfiguration.userController();
        }

        return instance;
    }

    public boolean issueTokensPair(HttpServletRequest req, HttpServletResponse resp,
                                   User user, Map<String, Object> claims) {

        Pair<JsonWebToken, JsonWebToken> pairToken = createJwsPair(user, claims);
        if (isNull(pairToken)) {
            user.setAuthorized(false);
            return false;
        }

        user.setAuthorized(true);
        req.getSession().setAttribute("user", user);
        req.getSession().setAttribute("jws", pairToken.getKey());

        Cookie refreshCookie = Optional.ofNullable(getCookie(req, "rsTokenAA")).orElse(null);
        Cookie fingerprintCookie = Optional.ofNullable(getCookie(req, "fingerprintAA")).orElse(null);

        if (refreshCookie == null)
            refreshCookie = new Cookie("rsTokenAA", "");

        if (fingerprintCookie == null)
            fingerprintCookie = new Cookie("fingerprintAA", "");

        refreshCookie.setValue(pairToken.getValue().getToken());
        fingerprintCookie.setValue(pairToken.getValue().getUserFingerprint());

        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setMaxAge(60 * 60 * 24 * 30);
        // refreshCookie.setDomain("localhost");
        refreshCookie.setPath("/");


        fingerprintCookie.setHttpOnly(true);
        fingerprintCookie.setSecure(true);
        fingerprintCookie.setMaxAge(60 * 60 * 24 * 30);
        // fingerprintCookie.setDomain("localhost");
        fingerprintCookie.setPath("/");


        resp.addCookie(refreshCookie);
        resp.addCookie(fingerprintCookie);

        return true;
    }

    public Pair<JsonWebToken, JsonWebToken> createJwsPair(User user, Map<String, Object> claims) {

        try {
            return createToken.create(user, claims);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JsonWebToken verifyJWT(String jws, String require) throws ExpiredJwtException, InvalidClaimException,
            NoSuchAlgorithmException, UnsupportedEncodingException {

        Map<String, Object> claims = verifyTokenJWS.verify(jws, require);

        if (claims == null)
            return null;

        JsonWebToken token = new JsonWebToken();
        token.setToken(jws);
        token.setUserFingerprint((String) claims.get("userFingerprint"));
        token.setUserId((Integer) claims.get("userId"));

        return token;
    }

    private JsonWebToken checkRsToken(String token, String fingerprint) {
        if (isNull(token) || isNull(fingerprint))
            return null;

        Integer id = getTokenId.get(token);

        if (isNull(id))
            return null;

        JsonWebToken jws = null;
        try {
            jws = verifyJWT(token, fingerprint);
            jws.setId(id);

        } catch (ExpiredJwtException expr) {
            removeTokenJWS.remove(id);
            expr.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jws;
    }

    public boolean refreshToken(ServletRequest req, ServletResponse resp) {
        String rsJws = Optional.ofNullable(getCookie(req, "rsTokenAA")).map(Cookie::getValue).orElse(null);
        String rsFing = Optional.ofNullable(getCookie(req, "fingerprintAA")).map(Cookie::getValue).orElse(null);

        JsonWebToken oldRefToken = checkRsToken(rsJws, rsFing);
        if (isNull(oldRefToken)) {
            return false;
        }

        removeToken(oldRefToken.getId());

        User user = (User) ((HttpServletRequest) req).getSession().getAttribute("user");
        if (user == null)
            user = userController.findUser(oldRefToken.getUserId());

        if(isNull(user))
            return false;

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());

        return issueTokensPair((HttpServletRequest) req, (HttpServletResponse) resp, user, claims);
    }

    public void removeTokenByUserId(Integer id) {
        removeTokenJWS.remove(id);
    }

    public void removeToken(Integer id) {
        removeTokenJWS.remove(id);
    }

    public void removeToken(String token) {
        removeTokenJWS.remove(token);
    }
}
