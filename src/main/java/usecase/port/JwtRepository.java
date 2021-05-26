package usecase.port;


import domain.entity.JsonWebToken;

public interface JwtRepository {

    boolean putToken(JsonWebToken token);

    Integer getTokenId(String token);

    void dropTokenById(Integer id);

    void dropTokenById(String token);
}
