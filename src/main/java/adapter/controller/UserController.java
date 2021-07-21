package adapter.controller;


import config.MyConfiguration;

import domain.entity.User;
import domain.entity.UserCard;import usecase.*;

import java.util.List;

public class UserController {
    private static UserController instance;

    private CreateUser createUser;
    private FindUser findUser;
    private LoginUser loginUser;
    private ConfirmUser confirmUser;
    private PasswordUpdate passwordUpdate;
    private UpdateUserCard updateUserCard;
    private RecommendUsersList recommendUsersList;
    private PutLikeAction putLikeAction;
    private UploadUserPhoto uploadUserPhoto;

    private UserController() {
    }

    public static UserController getController() {
        if (instance == null) {
            instance = new UserController();

            instance.createUser = MyConfiguration.createUser();
            instance.findUser = MyConfiguration.findUser();
            instance.loginUser = MyConfiguration.loginUser();
            instance.confirmUser = MyConfiguration.confirmUser();
            instance.passwordUpdate = MyConfiguration.passwordUpdate();
            instance.updateUserCard = MyConfiguration.updateUserCard();
            instance.recommendUsersList = MyConfiguration.recommendUsersList();
            instance.putLikeAction = MyConfiguration.putLikeAction();
        }

        return instance;
    }

    public int createUser(User user) {
        return createUser.create(user);
    }

    public User loginUser(String email, String password) {
        return loginUser.login(email, password);
    }

    public User findUser(int id) {
        return findUser.findUserById(id);
    }

    public User findUser(String email) {
        return findUser.findUserByEmail(email);
    }

    public void confirmUser(Integer id) {
        confirmUser.confirm(id);
    }

    public boolean passwordUpdate(Integer id, String password){
        return passwordUpdate.updatePassword(id, password);
    }

    public UserCard updateUserCard(UserCard card) {
        return updateUserCard.update(card);
    }

    public List<User> getRecommendUsersList(User user) {
        return recommendUsersList.get(user);
    }

    public void match(int from, int to) {
        putLikeAction.match(from, to);
    }

    public void like(int from, int to) {
        putLikeAction.like(from, to);
    }

    public void deleteLike(int from, int to) {
        putLikeAction.dislike(from, to);
    }
}
