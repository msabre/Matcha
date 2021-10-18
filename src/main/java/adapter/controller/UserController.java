package adapter.controller;


import config.MyConfiguration;

import config.MyProperties;
import domain.entity.FilterParams;
import domain.entity.Photo;
import domain.entity.User;
import domain.entity.UserCard;
import domain.entity.model.UserMatch;
import usecase.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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
    private UpdatePhotoParams updatePhotoParams;
    private UpdateFilter updateFilter;
    private UpdateEmail updateEmail;
    private FioUpdate fioUpdate;
    private BirthDateUpdate birthDateUpdate;
    private UploadPhotoContent uploadPhotoContent;
    private GetMatchList getMatchList;
    private ChatCreate chatCreate;

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
            instance.updatePhotoParams = MyConfiguration.updatePhotoParams();
            instance.updateFilter = MyConfiguration.updateFilter();
            instance.updateEmail = MyConfiguration.updateEmail();
            instance.fioUpdate = MyConfiguration.fioUpdate();
            instance.birthDateUpdate = MyConfiguration.birthDateUpdate();
            instance.uploadPhotoContent = MyConfiguration.uploadPhotoContent();
            instance.getMatchList = MyConfiguration.getMatchList();
            instance.chatCreate = MyConfiguration.chatCreate();
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
        return recommendUsersList.get(new ArrayList<>(), new ArrayList<>(), user);
    }

    public boolean putMatchOrLike(int from, int to) {
        return putLikeAction.putMatchOrLike(from, to);
    }

    public void deleteLike(int from, int to) {
        putLikeAction.deleteLike(from, to);
    }

    public void disLike(int from, int to) {
        putLikeAction.disLike(from, to);
    }

    public void updatePhotoParams(int userId, List<Photo> param) {
        updatePhotoParams.update(userId, param);
    }

    public void filterUpdate(FilterParams params) {
        updateFilter.update(params);
    }

    public void updateEmail(int id, String email) {
        updateEmail.update(id, email);
    }

    public void fioUpdate(int userId, String[] fio) {
        fioUpdate.update(userId, fio);
    }

    public void birthDateUpdate(int userId, Date birthDate, int yearsOld) {
        birthDateUpdate.update(userId, birthDate, yearsOld);
    }

    public void uploadPhotosContent(Collection<Photo> photos) {
        uploadPhotoContent.upload(photos);
    }

    public List<UserMatch> getUserMatchListWithSize(int id, int size) {
        return getMatchList.getN(id, size);
    }

    public List<UserMatch> getUserMatchListWithSizeAfterSpecificId(int id, int lastMatchId, int size) {
        return getMatchList.getNAfterSpecificId(id, lastMatchId, size);
    }

    public int createChatBetweenTwoUsers(int fromUsr, int toUsr) {
        return chatCreate.create(fromUsr, toUsr);
    }
}
