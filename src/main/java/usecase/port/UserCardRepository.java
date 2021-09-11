package usecase.port;

import domain.entity.Photo;
import domain.entity.UserCard;

import java.util.List;

public interface UserCardRepository {
    UserCard save(UserCard card);

    UserCard findById(Integer id);

    void increaseRating(int id, double increse);

    void updatePhotosParams(int cardId, List<Photo> photoList);

    void updateUserActions(UserCard userCard);
}
