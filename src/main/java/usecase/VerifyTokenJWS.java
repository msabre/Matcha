package usecase;

import config.MyProperties;
import domain.entity.JsonWebToken;
import io.jsonwebtoken.*;
import usecase.port.PasswordEncoder;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class VerifyTokenJWS {
    private final PasswordEncoder encoder;

    public VerifyTokenJWS(PasswordEncoder passwordEncoder) {
        this.encoder = passwordEncoder;
    }

    public Map<String, Object> verify(String jws, String requireParam) throws ExpiredJwtException, InvalidClaimException,
            NoSuchAlgorithmException, UnsupportedEncodingException {

        String requireHash = encoder.getSHA256(requireParam);

        Jws<Claims> claimsJws = Jwts
                .parserBuilder()
                .require("userFingerprint", requireHash)
                .setSigningKey(MyProperties.JWT_KEY)
                .build()
                .parseClaimsJws(jws);

        Map<String, Object> mapClaims = new HashMap<>();
        mapClaims.put("userId", claimsJws.getBody().get("userId"));
        mapClaims.put("userFingerprint", claimsJws.getBody().get("userFingerprint"));

        return mapClaims;
    }
}
