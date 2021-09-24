package usecase;

import domain.entity.Photo;
import usecase.port.UserCardRepository;

public class GetUserIcon {

    private final UserCardRepository userCardRepository;

    public GetUserIcon(UserCardRepository userCardRepository) {
        this.userCardRepository = userCardRepository;
    }

    public Photo get(int userId) {
        return userCardRepository.getUserIconById(userId);
    }
}
