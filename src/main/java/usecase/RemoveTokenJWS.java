package usecase;

import usecase.port.JwtRepository;

public class RemoveTokenJWS {

    private final JwtRepository repository;

    public RemoveTokenJWS(JwtRepository jwtRepository) {
        this.repository = jwtRepository;
    }

    public void removeByUserId(Integer id) {
        repository.dropTokenByUserId(id);
    }

    public void remove(Integer id) {
           repository.dropTokenById(id);
    }

    public void remove(String token) {
        repository.dropTokenById(token);
    }

}
