package usecase;

import usecase.port.JwtRepository;

public class GetTokenId {

    private final JwtRepository jwtRepository;

    public GetTokenId(JwtRepository jwtRepository) {
        this.jwtRepository = jwtRepository;
    }

    public Integer get(String token) {

        return jwtRepository.getTokenId(token);
    }
}
