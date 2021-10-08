package usecase;

import config.MyProperties;
import domain.entity.JsonWebToken;
import domain.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javafx.util.Pair;
import usecase.port.JwtRepository;
import usecase.port.PasswordEncoder;

import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class CreateTokenJWS {

    private final JwtRepository jwtRepository;
    private final PasswordEncoder encoder;

    public CreateTokenJWS(JwtRepository jwtRepository, PasswordEncoder encoder) {
        this.jwtRepository = jwtRepository;
        this.encoder = encoder;
    }

    public JsonWebToken getAccessToken(User user, Map<String, Object> claims, int minute) {
        Calendar c = Calendar.getInstance();

        c.add(Calendar.MINUTE, minute);
        Date accessDate = c.getTime();
        JsonWebToken access;
        try {
            access = getToken(user, claims, accessDate);
        } catch (Exception e) {
            return null;
        }
        return access;
    }

    public Pair<JsonWebToken, JsonWebToken> create(User user, Map<String, Object> claims, int minute, int days) {

        Calendar c = Calendar.getInstance();

        c.add(Calendar.MINUTE, minute);
        Date accessDate = c.getTime();

        c.add(Calendar.DAY_OF_YEAR, days);
        Date refreshDate = c.getTime();

        JsonWebToken access;
        JsonWebToken refresh;
        try {
            access = getToken(user, claims, accessDate);
            refresh = getToken(user, claims, refreshDate);

            jwtRepository.putToken(refresh);

        } catch (Exception e) {
            return null;
        }

        return new Pair<>(access, refresh);
    }

    private JsonWebToken getToken(User user, Map<String, Object> claims, Date expirationDate)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {

        JsonWebToken jsonWebToken = new JsonWebToken();
        jsonWebToken.setUserId(user.getId());

        SecureRandom random = new SecureRandom();
        byte[] randomFgp = new byte[20];
        random.nextBytes(randomFgp);

        String userFingerprint = DatatypeConverter.printHexBinary(randomFgp);
        jsonWebToken.setUserFingerprint(userFingerprint);

        // Получаем хэш отпечатка
        String userFingerprintHash = encoder.getSHA256(userFingerprint);
        claims.put("userFingerprint", userFingerprintHash);
        claims.put("userId", user.getId());

        Calendar c = Calendar.getInstance();
        Date now = c.getTime();

        System.out.println("token.getUserFingerprint =" + userFingerprint);
        String encodeJws = Jwts.builder().
                setIssuedAt(now).
                setNotBefore(now).
                setExpiration(expirationDate).
                addClaims(claims).
                signWith(MyProperties.JWT_KEY).
                compact();

        jsonWebToken.setToken(encodeJws);

        return jsonWebToken;
    }
}
