package usecase;

import config.MyProperties;
import domain.entity.User;

import usecase.port.LikesActionRepository;
import usecase.port.UserRepository;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static config.MyProperties.USERS_LIST_SIZE;

public class RecommendUsersList {

    private Map<String, List<String>> sexualConformity;
    private User user;

    private final UserRepository userRepository;
    private final LikesActionRepository likesActionRepository;

    public RecommendUsersList(UserRepository userRepository, LikesActionRepository likesActionRepository) {
        this.userRepository = userRepository;
        this.likesActionRepository = likesActionRepository;

        String path = MyProperties.class.getResource("/sexualPreference.properties").getPath();

        FileInputStream fileInputStream;
        Properties prop = new Properties();

        try {
            fileInputStream = new FileInputStream(path);
            prop.load(fileInputStream);

            sexualConformity = new HashMap<>();
            for (Object key : prop.keySet()) {
                String value = prop.getProperty(key.toString());
                List<String> arr = Arrays.asList(value.split(","));
                sexualConformity.put(key.toString(), arr);
            }

        } catch (IOException e) {
            System.out.println("Не удалось загрузить файл");
        }
    }

    public List<User> get(List<User> resultList, List<User> alreadyFind, User user) {
        this.user = user;

        List<User> newUserList = getNewForActionUsers(alreadyFind);
        if (newUserList.isEmpty() && (newUserList = getDislikesUsers(alreadyFind)).isEmpty()) // Выход из рекурсии
            return resultList;

        alreadyFind.addAll(newUserList);

        newUserList = customFilter(newUserList);
        resultList.addAll(newUserList);
        if (resultList.size() < USERS_LIST_SIZE)
            return get(resultList, alreadyFind, user);

        fixSize(resultList);
        sortUserList(resultList);

        return resultList;
    }

    private List<User> customFilter(List<User> userList) {
        if (user.getFilter().getCommonTagsCount() > 0)
        {
            int countSameTags = user.getFilter().getCommonTagsCount();
            userList = userList.stream()
                    .filter(userObj -> getCommonTagsCount(userObj) >= countSameTags)
                    .collect(Collectors.toList());

            if (userList.size() == 0)
                return userList;
        }

        if (user.getFilter().getRating() > 0.0)
        {
            double rating = user.getFilter().getRating();
            userList = userList.stream().filter(userObj ->
                    userObj.getCard().getRating() >= rating - MyProperties.RATING_FALSITY)
                    .collect(Collectors.toList());

            if (userList.size() == 0)
                return userList;
        }

        return userList;
    }

    private void sortUserList(List<User> userList) {
        userList.sort(
                Comparator.comparing(User::getYearsOld)
                .thenComparing(u -> u.getCard().getRating())
                .thenComparing(this::getCommonTagsCount)
        );
    }

    private void fixSize(List<User> userList) {
        List<Integer> dislikesByIds = likesActionRepository.getToUserDislikesByIds(user.getId(), userList.stream().map(User::getId).collect(Collectors.toList()));
        List<User> dislikesUsers = userList.stream()
                .filter(userObj-> dislikesByIds.contains(userObj.getId())) // Отсортированы в базе
                .sorted(Comparator.comparingInt(u -> dislikesByIds.indexOf(u.getId())))
                .collect(Collectors.toList());

        userList.removeAll(dislikesUsers);

        if (userList.size() >= USERS_LIST_SIZE)
            userList.subList(USERS_LIST_SIZE, userList.size()).clear();

        for (int i = 0; i < dislikesUsers.size() && userList.size() < USERS_LIST_SIZE; i++) {
            userList.add(dislikesUsers.get(i));
        }
    }

    private List<String> getUserSexualPreferences() {
        return sexualConformity.get(String.format("%s;%s", user.getCard().getGender().getValue(),
                user.getCard().getSexualPreference().getValue()));
    }

    private List<User> getNewForActionUsers(List<User> resultList) {
        return userRepository.getNewForActionUsersWithParams(resultList.stream().map(User::getId).collect(Collectors.toList()),
                user.getFilter().getLocation(),
                user.getId(),
                user.getFilter().getAgeBy(),
                user.getFilter().getAgeTo(),
                getUserSexualPreferences(),
                USERS_LIST_SIZE * 3);
    }

    private List<User> getDislikesUsers(List<User> resultList) {
        return userRepository.getDislikeUsersWithParams(resultList.stream().map(User::getId).collect(Collectors.toList()),
                user.getFilter().getLocation(),
                user.getId(),
                user.getFilter().getAgeBy(),
                user.getFilter().getAgeTo(),
                getUserSexualPreferences(),
                USERS_LIST_SIZE * 3);
    }

    private int getCommonTagsCount(User userObj) {
        int count = 0;
        for (String tag : userObj.getCard().getTags()) {
            if (user.getCard().getTags().contains(tag)) {
                count++;
            }
        }
        return count;
    }
}
