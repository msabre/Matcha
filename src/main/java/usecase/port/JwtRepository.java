package usecase.port;

import domain.entity.JsonWebToken;
import domain.entity.model.types.JwtType;

public interface JwtRepository {

    boolean putToken(JsonWebToken token, JwtType type);

    Integer getTokenId(String token);

    void dropToken(Integer id);

    void dropToken(String token);

    void dropTokenByUserId(Integer userId, JwtType type);
}
