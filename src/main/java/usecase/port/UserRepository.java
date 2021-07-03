package usecase.port;

import domain.entity.User;
import usecase.exception.SamePasswordException;

import java.util.List;

public interface UserRepository {
    int save(User user);

    User findByEmail(String email);

    User findById(Integer id);

    void confirmById(Integer id);

    boolean passwordUpdate(Integer id, String password);

    List<User> getAllUserInSameLocation(String location);

    void createChatBetweenTwoUsers(int usr1, int usr2);
}
