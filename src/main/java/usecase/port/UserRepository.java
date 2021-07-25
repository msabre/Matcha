package usecase.port;

import domain.entity.Photo;
import domain.entity.User;
import domain.entity.UserCard;

import java.util.List;

public interface UserRepository {
    int save(User user);

    User findByEmail(String email);

    User findById(int id);

    void uploadPhotosContetn(UserCard card);

    void setPhotosParams(List<Photo> photos);

    void confirmById(int id);

    boolean passwordUpdate(int id, String password);

    List<User> getAllUserInSameLocation(String location, int id);

    void createChatBetweenTwoUsers(int usr1, int usr2);
}
