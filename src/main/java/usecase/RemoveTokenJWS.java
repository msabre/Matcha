package usecase;

import domain.entity.model.types.JwtType;
import usecase.port.JwtRepository;

public class RemoveTokenJWS {

    private final JwtRepository repository;

    public RemoveTokenJWS(JwtRepository jwtRepository) {
        this.repository = jwtRepository;
    }

    public void removeByUserId(Integer userId, JwtType type) {
        repository.dropTokenByUserId(userId, type);
    }

    public void remove(int id) {
           repository.dropToken(id);
    }

    public void remove(String token) {
        repository.dropToken(token);
    }

}
