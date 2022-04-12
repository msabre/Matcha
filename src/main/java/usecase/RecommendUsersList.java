package usecase;

import config.MyProperties;
import domain.entity.User;

import domain.entity.model.types.Action;
import usecase.port.LikesActionRepository;
import usecase.port.UserRepository;

import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class RecommendUsersList {

    private Map<String, List<String>> sexualConformity;
    private User user;

    private final UserRepository userRepository;
    private final LikesActionRepository likesActionRepository;

    public RecommendUsersList(UserRepository userRepository, LikesActionRepository likesActionRepository) {
        this.userRepository = userRepository;
        this.likesActionRepository = likesActionRepository;

        FileInputStream fileInputStream;
        Properties prop = new Properties();

        try {
            String path = Paths.get(RecommendUsersList.class.getResource("/sexualPreference.properties").toURI()).toFile().getPath();
            fileInputStream = new FileInputStream(path);
            prop.load(fileInputStream);

            sexualConformity = new HashMap<>();
            for (Object key : prop.keySet()) {
                String value = prop.getProperty(key.toString());
                List<String> arr = Arrays.asList(value.split(","));
                sexualConformity.put(key.toString(), arr);
            }

        } catch (Exception e) {
            System.out.println("Не удалось загрузить файл");
        }
    }

    public List<User> get(List<User> resultList, List<User> alreadyFind, User user, int userListSize) {
        this.user = user;

        List<User> newUserList = getNewForActionUsers(alreadyFind, userListSize);
        if (newUserList.isEmpty() && (newUserList = getDislikesUsers(alreadyFind, userListSize)).isEmpty()) // Выход из рекурсии
            return resultList;

        alreadyFind.addAll(newUserList);

        newUserList = blockFilter(newUserList, user.getId());
        newUserList = customFilter(newUserList);

        resultList.addAll(newUserList);
        if (resultList.size() < userListSize)
            return get(resultList, alreadyFind, user, userListSize);

        fixSize(resultList, userListSize);
        sortUserList(resultList);

        return resultList;
    }

    private List<User> blockFilter(List<User> userList, int userId) {
        List<Integer> actions = likesActionRepository
                .getByFromUsrOrToUsrAndAction(userId, Action.BLOCK.getValue())
                .stream()
                .map(act -> (act.getFromUsr() != userId) ? act.getFromUsr() : act.getToUsr())
                .collect(Collectors.toList());

        return userList.stream().filter(usr -> !actions.contains(usr.getId())).collect(Collectors.toList());
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

    private void fixSize(List<User> userList, int userListSize) {
        List<Integer> dislikesByIds = likesActionRepository.getToUserDislikesByIds(user.getId(), userList.stream().map(User::getId).collect(Collectors.toList()));
        List<User> dislikesUsers = userList.stream()
                .filter(userObj-> dislikesByIds.contains(userObj.getId())) // Отсортированы в базе
                .sorted(Comparator.comparingInt(u -> dislikesByIds.indexOf(u.getId())))
                .collect(Collectors.toList());

        userList.removeAll(dislikesUsers);

        if (userList.size() >= userListSize)
            userList.subList(userListSize, userList.size()).clear();

        for (int i = 0; i < dislikesUsers.size() && userList.size() < userListSize; i++) {
            userList.add(dislikesUsers.get(i));
        }
    }

    private List<String> getUserSexualPreferences() {
        return sexualConformity.get(String.format("%s;%s", user.getCard().getGender().getValue(),
                user.getCard().getSexualPreference().getValue()));
    }

    private List<User> getNewForActionUsers(List<User> resultList, int userListSize) {
        return userRepository.getNewForActionUsersWithParams(resultList.stream().map(User::getId).collect(Collectors.toList()),
                user.getFilter().getLocation(),
                user.getId(),
                user.getFilter().getAgeBy(),
                user.getFilter().getAgeTo(),
                getUserSexualPreferences(),
                userListSize * 3);
    }

    private List<User> getDislikesUsers(List<User> resultList, int userListSize) {
        return userRepository.getDislikeUsersWithParams(resultList.stream().map(User::getId).collect(Collectors.toList()),
                user.getFilter().getLocation(),
                user.getId(),
                user.getFilter().getAgeBy(),
                user.getFilter().getAgeTo(),
                getUserSexualPreferences(),
                userListSize * 3);
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
