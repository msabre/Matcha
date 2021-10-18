package usecase.port;

import domain.entity.User;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public interface UserRepository {
    int save(User user);

    User findByEmail(String email);

    User findById(int id);

    void confirmById(int id);

    boolean passwordUpdate(int id, String password);

    LinkedList<User> getNewForActionUsersWithParams(List<Integer> currentIds, String location, int id, int age_by, int age_to, List<String> preferencesParams, int limit);

    LinkedList<User> getDislikeUsersWithParams(List<Integer> currentIds, String location, int id, int age_by, int age_to, List<String> preferencesParams, int limit);

    void updateEmail(int id, String email);

    void updateFio(int id, String[] fio);

    void birhDateUpdate(int id, Date birthDate, int yearsOld);
}
