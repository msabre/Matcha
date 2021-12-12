package usecase.port;

import domain.entity.User;
import usecase.exception.EmailBusyException;
import usecase.exception.UserNameBusyException;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface UserRepository {
    int save(User user) throws UserNameBusyException, EmailBusyException;

    User findByEmail(String email);

    User findByUsername(String username);

    User findById(int id);

    void confirmById(int id);

    boolean passwordUpdate(int id, String password);

    LinkedList<User> getNewForActionUsersWithParams(List<Integer> currentIds, String location, int id, int age_by, int age_to, List<String> preferencesParams, int limit);

    LinkedList<User> getDislikeUsersWithParams(List<Integer> currentIds, String location, int id, int age_by, int age_to, List<String> preferencesParams, int limit);

    void updateEmail(int id, String email);

    void updateFio(int id, String[] fio);

    void birthDateUpdate(int id, Date birthDate, int yearsOld);

    List<Integer> getNUserIdsWithFreeChatByIds(String ids, int limit);

    Map<Integer, String> getUserNamesByIds(List<Integer> userIds);
}
