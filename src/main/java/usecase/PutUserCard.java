package usecase;

import domain.entity.UserCard;
import usecase.port.UserCardRepository;

public class PutUserCard {
    private UserCardRepository repository;


    public PutUserCard(UserCardRepository repository) {
        this.repository = repository;
    }

    public void create(UserCard card) {
         repository.save(card);
    }
}
