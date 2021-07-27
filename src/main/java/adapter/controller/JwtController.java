package adapter.controller;

import application.services.HttpService;
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
        Pair<String, String> tokenStr = getAcToken(req);

        if (isNull(tokenStr)) {
            JsonWebToken newToken = refreshToken(req, resp);
            return (newToken == null) ? -1 : newToken.getUserId();
        }

        JsonWebToken jws;
        try {
            jws = verifyJWT(tokenStr.getKey(), tokenStr.getValue());
        } catch (ExpiredJwtException expiredJwtException) {
            if ((jws = refreshToken(req, resp)) == null)
                return -1;
        } catch (Exception e) {
            return -1;
        }

        return jws.getUserId();
    }

    public Pair<String, String> getRsToken(ServletRequest req) {
        return getJwsFromCookies(req, REFRESH_TOKEN, FINGERPRINT_REFRESH);
    }

    public Pair<String, String> getAcToken(ServletRequest req) {
        return getJwsFromCookies(req, ACCESS_TOKEN, FINGERPRINT_ACCESS);
    }

    private Pair<String, String> getJwsFromCookies(ServletRequest req, String token, String requireParam) {
        String jws = Optional.ofNullable(getCookie(req, token)).map(Cookie::getValue).orElse(null);
        String fingerprint = Optional.ofNullable(getCookie(req, requireParam)).map(Cookie::getValue).orElse(null);

        if (isNull(jws) || isNull(fingerprint))
            return null;

        return new Pair<>(jws, fingerprint);
    }

    public void deleteJwtCookies(ServletRequest req, ServletResponse resp) {
        Cookie accessCookie = deleteCookie(HttpService.getCookie(req, ACCESS_TOKEN));
        Cookie fingerprintAcCookie = deleteCookie(HttpService.getCookie(req, FINGERPRINT_REFRESH));
        Cookie refreshCookie = deleteCookie(HttpService.getCookie(req, REFRESH_TOKEN));
        Cookie fingerprintRsCookie = deleteCookie(HttpService.getCookie(req, FINGERPRINT_ACCESS));

        HttpServletResponse response = (HttpServletResponse) resp;
        addCookie(refreshCookie, response);
        addCookie(accessCookie, response);
        addCookie(fingerprintAcCookie, response);
        addCookie(fingerprintRsCookie, response);
    }


    private Cookie deleteCookie(Cookie cookie) {
        if (cookie != null)
            cookie.setMaxAge(0);
        return cookie;
    }

    private void addCookie(Cookie cookie, HttpServletResponse response) {
        if (cookie != null)
            response.addCookie(cookie);
    }

    private JsonWebToken refreshToken(ServletRequest req, ServletResponse resp) {
        Pair<String, String> rsJws = getRsToken(req);
        if (isNull(rsJws) || isNull(rsJws.getKey()) || isNull(rsJws.getValue()))
            return null;

        JsonWebToken oldRefToken = checkRsToken(rsJws.getKey(), rsJws.getValue());
        if (isNull(oldRefToken)) {
            return null;
        }

        removeToken(oldRefToken.getId());

        User user = (User) ((HttpServletRequest) req).getSession().getAttribute("user");
        if (user == null)
            user = userController.findUser(oldRefToken.getUserId());

        if(isNull(user)) {
            return null;
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());

        Pair<JsonWebToken, JsonWebToken> tokens = issueTokensPair((HttpServletRequest) req, (HttpServletResponse) resp, user, claims);
        return Optional.ofNullable(tokens)
                .map(Pair::getKey)
                .orElse(null);
    }

    private JsonWebToken checkRsToken(String token, String fingerprint) {
        Integer id = getTokenId.get(token);
        if (isNull(id))
            return null;

        JsonWebToken jws;
        try {
            jws = verifyJWT(token, fingerprint);
            jws.setId(id);

        } catch (ExpiredJwtException expr) {
            removeTokenJWS.remove(id);
            throw expr;

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

        int cookiesAcExpires = 30;
        int cookiesRsExpires = 60 * 60 * 24 * 45;

        Cookie accessCookie = createHttpOnlyCookie(req, ACCESS_TOKEN, pairToken.getKey().getToken(), cookiesAcExpires);
        Cookie fingerprintAcCookie = createHttpOnlyCookie(req, FINGERPRINT_ACCESS, pairToken.getKey().getUserFingerprint(), cookiesAcExpires);

        Cookie refreshCookie = createHttpOnlyCookie(req, REFRESH_TOKEN, pairToken.getValue().getToken(), cookiesRsExpires);
        Cookie fingerprintRsCookie = createHttpOnlyCookie(req, FINGERPRINT_REFRESH, pairToken.getValue().getUserFingerprint(), cookiesRsExpires);

        resp.addCookie(accessCookie);
        resp.addCookie(refreshCookie);
        resp.addCookie(fingerprintAcCookie);
        resp.addCookie(fingerprintRsCookie);

        return pairToken;
    }

    private Cookie createHttpOnlyCookie(HttpServletRequest req, String name, String value, int maxAge) {
        Cookie cookie = Optional.ofNullable(getCookie(req, name)).orElse(null);
        if (cookie == null)
            cookie = new Cookie(name, "");

        cookie.setValue(value);
        cookie.setHttpOnly(true);
        //cookie.setSecure(true);
//        cookie.setDomain("/");
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);

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
