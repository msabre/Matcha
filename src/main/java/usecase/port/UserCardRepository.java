package usecase.port;

import domain.entity.Photo;
import domain.entity.UserCard;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;

public interface UserCardRepository {
    UserCard save(UserCard card);

    UserCard findById(Integer id);

    void increaseRating(int id, double increase);

    void decreaseRating(int id, double decrease);

    void updatePhotosParams(int cardId, String params);

    void updateMainPhoto(int cardId, Integer main);

    void updateUserActions(UserCard userCard);

    List<Photo> getIconsByIds(Collection<Integer> ids);

    Integer getActualMain(Connection connection, int userCardId);
}
