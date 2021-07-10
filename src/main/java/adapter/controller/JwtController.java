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
import static java.util.Objects.nonNull;

public class JwtController {
    private static JwtController instance;

    private UserController userController;

    private CreateTokenJWS createToken;
    private VerifyTokenJWS verifyTokenJWS;
    private GetTokenId getTokenId;
    private RemoveTokenJWS removeTokenJWS;

    private final String ACCESS_TOKEN = "acTokenAA";
    private final String REFRESH_TOKEN = "rsTokenAA";
    private final String FINGERPRINT_ACCESS = "fingerprintAcAA";
    private final String FINGERPRINT_REFRESH = "fingerprintRsAA";

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

    public int checkJwt(ServletRequest req, ServletResponse resp) {
        JsonWebToken jws = getJwsFromCookies(req);

        if (isNull(jws)) {
            JsonWebToken newToken = refreshToken(req, resp);
            return (newToken == null) ? -1 : newToken.getUserId();
        }

        try {
            verifyJWT(jws.getToken(), jws.getUserFingerprint());

        } catch (ExpiredJwtException expiredJwtException) {
            if (refreshToken(req, resp) == null)
                return -1;
        } catch (Exception e) {
            return -1;
        }

        return jws.getUserId();
    }

    private JsonWebToken getJwsFromCookies(ServletRequest req) {
        String accessJws = Optional.ofNullable(getCookie(req, ACCESS_TOKEN)).map(Cookie::getValue).orElse(null);
        String rsFing = Optional.ofNullable(getCookie(req, FINGERPRINT_ACCESS)).map(Cookie::getValue).orElse(null);

        if (isNull(accessJws) || isNull(rsFing))
            return null;

        JsonWebToken jws = new JsonWebToken();
        jws.setToken(accessJws);
        jws.setUserFingerprint(rsFing);

        return jws;
    }

    private JsonWebToken refreshToken(ServletRequest req, ServletResponse resp) {
        String rsJws = Optional.ofNullable(getCookie(req, REFRESH_TOKEN)).map(Cookie::getValue).orElse(null);
        String rsFing = Optional.ofNullable(getCookie(req, FINGERPRINT_REFRESH)).map(Cookie::getValue).orElse(null);

        JsonWebToken oldRefToken = checkRsToken(rsJws, rsFing);
        if (isNull(oldRefToken)) {
            return null;
        }

        removeToken(oldRefToken.getId());

        User user = (User) ((HttpServletRequest) req).getSession().getAttribute("user");
        if (user == null)
            user = userController.findUser(oldRefToken.getUserId());

        if(isNull(user))
            return null;

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());

        Pair<JsonWebToken, JsonWebToken> tokens = issueTokensPair((HttpServletRequest) req, (HttpServletResponse) resp, user, claims);
        return Optional.ofNullable(tokens)
                .map(Pair::getKey)
                .orElse(null);
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

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return jws;
    }

    public Pair<JsonWebToken, JsonWebToken> issueTokensPair(HttpServletRequest req, HttpServletResponse resp,
                                   User user, Map<String, Object> claims) {

        Pair<JsonWebToken, JsonWebToken> pairToken = createJwsPair(user, claims);
        if (isNull(pairToken)) {
            user.setAuthorized(false);
            return null;
        }

        user.setAuthorized(true);
        req.getSession().setAttribute("user", user);

        Cookie accessCookie = createHttpOnlyCookie(req, ACCESS_TOKEN, pairToken.getKey().getToken());
        Cookie refreshCookie = createHttpOnlyCookie(req, REFRESH_TOKEN, pairToken.getValue().getToken());
        Cookie fingerprintRsCookie = createHttpOnlyCookie(req, FINGERPRINT_ACCESS, pairToken.getKey().getUserFingerprint());
        Cookie fingerprintAcCookie = createHttpOnlyCookie(req, FINGERPRINT_REFRESH, pairToken.getValue().getUserFingerprint());

        resp.addCookie(accessCookie);
        resp.addCookie(refreshCookie);
        resp.addCookie(fingerprintAcCookie);
        resp.addCookie(fingerprintRsCookie);

        return pairToken;
    }

    private Cookie createHttpOnlyCookie(HttpServletRequest req, String name, String value) {
        Cookie cookie = Optional.ofNullable(getCookie(req, name)).orElse(null);
        if (cookie == null)
            cookie = new Cookie(name, "");

        cookie.setValue(value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");

        return cookie;

    }

    private Pair<JsonWebToken, JsonWebToken> createJwsPair(User user, Map<String, Object> claims) {

        try {
            return createToken.create(user, claims);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JsonWebToken verifyJWT(String jws, String require) throws ExpiredJwtException, InvalidClaimException,
            NoSuchAlgorithmException, UnsupportedEncodingException {

        Map<String, Object> claims = verifyTokenJWS.verify(jws, require);

        JsonWebToken token = new JsonWebToken();
        token.setToken(jws);
        token.setUserFingerprint((String) claims.get("userFingerprint"));
        token.setUserId((Integer) claims.get("userId"));

        return token;
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
