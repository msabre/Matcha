package usecase;

import domain.entity.Link;
import domain.entity.UserCard;
import usecase.port.UserCardRepository;

public class UpdateUserCard {
    private final UserCardRepository repository;

    public UpdateUserCard(UserCardRepository repository) {
        this.repository = repository;
    }

    public UserCard update(UserCard card) {
        return repository.save(card);
    }
}
