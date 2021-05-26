package usecase.port;

import domain.entity.Link;
import domain.entity.UserCard;

public interface UserCardRepository {
    UserCard save(UserCard card);

    UserCard findById(Integer id);

    void increaseRating(int id, double increse);
}
