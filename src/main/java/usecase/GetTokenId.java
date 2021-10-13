package usecase;

import usecase.port.JwtRepository;

public class GetTokenId {

    private final JwtRepository jwtRepository;

    public GetTokenId(JwtRepository jwtRepository) {
        this.jwtRepository = jwtRepository;
    }

    public Integer get(String token) throws Exception {
        Integer tokenId = jwtRepository.getTokenId(token);
        if (tokenId == null)
            throw new Exception();
        return tokenId;
    }
}
