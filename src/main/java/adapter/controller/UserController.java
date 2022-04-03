package adapter.controller;


import config.MyConfiguration;

import domain.entity.*;
import domain.entity.model.ActionHistory;
import domain.entity.model.OnlineStatus;
import domain.entity.model.types.Action;
import usecase.*;
import usecase.exception.EmailBusyException;
import usecase.exception.UserNameBusyException;

import java.time.ZoneId;
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
    private UpdatePhotoSettings updatePhotoParams;
    private UpdateFilter updateFilter;
    private UpdateUser updateUser;
    private FioUpdate fioUpdate;
    private UserNameUpdate usernameUpdate;
    private BirthDateUpdate birthDateUpdate;
    private UploadPhotoContent uploadPhotoContent;
    private GetHistoryActionList getHistoryActionList;
    private ChatCreate chatCreate;
    private GetUserFields getUserFields;
    private LeadTimeToZone leadTimeToZone;

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
            instance.updateUser = MyConfiguration.updateEmail();
            instance.fioUpdate = MyConfiguration.fioUpdate();
            instance.usernameUpdate = MyConfiguration.userNameUpdate();
            instance.birthDateUpdate = MyConfiguration.birthDateUpdate();
            instance.uploadPhotoContent = MyConfiguration.uploadPhotoContent();
            instance.getHistoryActionList = MyConfiguration.getMatchList();
            instance.chatCreate = MyConfiguration.chatCreate();
            instance.getUserFields = MyConfiguration.getUserFields();
            instance.leadTimeToZone = MyConfiguration.leadTimeToZone();
        }

        return instance;
    }

    public int createUser(User user) throws UserNameBusyException, EmailBusyException {
        return createUser.create(user);
    }

    public User loginUser(String login, String password) {
        return loginUser.login(login, password);
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

    public List<User> getRecommendUsersList(User user, int userListSize) {
        return recommendUsersList.get(new ArrayList<>(), new ArrayList<>(), user, userListSize);
    }

    public boolean putMatchOrLike(LikeAction likeAction) {
        return putLikeAction.putMatchOrLike(likeAction);
    }

    public void takeLike(LikeAction likeAction) {
        putLikeAction.deleteLike(likeAction);
    }

    public void disLike(LikeAction likeAction) {
        putLikeAction.disLike(likeAction);
    }

    public void visit(LikeAction likeAction) {
        putLikeAction.fixVisit(likeAction);
    }

    public void block(LikeAction likeAction) {
        putLikeAction.block(likeAction);
    }

    public boolean fake(LikeAction likeAction) {
        return putLikeAction.fake(likeAction);
    }

    public void takeFake(LikeAction likeAction) {
        putLikeAction.takeFake(likeAction);
    }

    public void updatePhotoParams(int userId, String param) {
        updatePhotoParams.updateParams(userId, param);
    }

    public void updateMainPhoto(int userId, Integer mainPhoto) {
        updatePhotoParams.updateMain(userId, mainPhoto);
    }

    public void filterUpdate(FilterParams params) {
        updateFilter.update(params);
    }

    public void updateEmail(int id, String email) {
        updateUser.email(id, email);
    }

    public void fioUpdate(int userId, String[] fio) {
        fioUpdate.update(userId, fio);
    }

    public void usernameUpdate(int userId, String username) {
        usernameUpdate.update(userId, username);
    }

    public void birthDateUpdate(int userId, Date birthDate, int yearsOld) {
        birthDateUpdate.update(userId, birthDate, yearsOld);
    }

    public void uploadPhotosContent(List<Photo> photos) {
        uploadPhotoContent.upload(photos);
    }

    public void uploadMainPhotoContent(Photo photo) {
        uploadPhotoContent.uploadMain(photo);
    }

    public List<ActionHistory> getNMatchesWithoutDialogs(int id, int size) {
        return getHistoryActionList.getNMatchesWithoutDialogs(id, size);
    }

    public List<ActionHistory> getNMatchesWithoutDialogsAfterId(int id, int lastMatchId, int size) {
        return getHistoryActionList.getNMatchesWithoutDialogsAfterId(id, lastMatchId, size);
    }

    public List<ActionHistory> getFromActions(Action action, int id, int size) {
        return getHistoryActionList.getNActions(action, id, size);
    }

    public List<ActionHistory> getFromActionsAfterId(Action action, int id, int lastMatchId, int size) {
        return getHistoryActionList.getNActionsAfterId(action, id, lastMatchId, size);
    }

    public List<ActionHistory> getToActions(Action action, int id, int size) {
        return getHistoryActionList.getNtoUser(action, id, size);
    }

    public List<ActionHistory> getToActionsAfterId(Action action, int id, int lastMatchId, int size) {
        return getHistoryActionList.getNtoUserAfterId(action, id, lastMatchId, size);
    }

    public void updateStatus(int userId, ZoneId zoneId, OnlineStatus.Status status) {
        updateUser.status(userId, zoneId, status);
    }
    
    public List<OnlineStatus> getUserStatusesByIds(Integer[] ids) {
        return getUserFields.getStatusByIds(ids);
    }

    public void leadLastActionToLocationTimeUser(List<User> users, String location) {
        leadTimeToZone.lastActionToLocationTimeUser(users, location);
    }

    public void leadLastActionToLocationTimeStatus(List<OnlineStatus> statusList, String location) {
        leadTimeToZone.lastActionToLocationTimeStatus(statusList, location);
    }
    
    public int createChatBetweenTwoUsers(int fromUsr, int toUsr) {
        return chatCreate.create(fromUsr, toUsr);
    }
}
