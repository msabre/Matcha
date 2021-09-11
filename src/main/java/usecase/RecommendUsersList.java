package usecase;

import config.MyProperties;
import domain.entity.User;

import usecase.port.LikesActionRepository;
import usecase.port.UserCardRepository;
import usecase.port.UserRepository;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RecommendUsersList {

    private Map<String, List<String>> sexualConformity;
    private User user;

    private final UserRepository userRepository;
    private final LikesActionRepository likesActionRepository;
    private final UserCardRepository userCardRepository;

    public RecommendUsersList(UserRepository userRepository, LikesActionRepository likesActionRepository, UserCardRepository userCardRepository) {
        this.userRepository = userRepository;
        this.likesActionRepository = likesActionRepository;
        this.userCardRepository = userCardRepository;

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

    public List<User> get(User user) {
        this.user = user;
        List<User> userList = userRepository.getAllUserInSameLocation(
                user.getFilter().getLocation(),
                user.getId(),
                user.getFilter().getAgeBy(),
                user.getFilter().getAgeTo(),
                sexualConformity.get(
                        String.format("%s;%s",
                        user.getCard().getGender().getValue(),
                        user.getCard().getSexualPreference().getValue())));

        if (userList == null)
            return null;

        userCardRepository.updateUserActions(user.getCard());
        userList = customFilter(userList);
        if (userList == null)
            return null;

        fixSize(userList);
        sortUserList(userList);

        // ставим матч если кто то из пользователей уже лайкнул клиента
        userList.forEach((userObj) -> {
            if (user.getCard().getLikes().contains(userObj.getId()))
                userObj.setMatch(true);
        });

        // поставили всем пользователям дизлайк
        likesActionRepository.putDislikeForUsers(user.getId(),
                userList.stream().map(User::getId).collect(Collectors.toList()),
                user.getCard().getDisLikes()
        );

        return userList;
    }

    private List<User> customFilter(List<User> userList) {

        // Фильтр на количетсво общих интересов
        if (user.getFilter().getCommonTagsCount() > 0)
        {
            int countSameTags = user.getFilter().getCommonTagsCount();
            userList = userList.stream()
                    .filter(userObj -> getCommonTagsCount(userObj) >= countSameTags)
                    .collect(Collectors.toList());

            if (userList.size() == 0)
                return null;
        }

        // Фильтр на рейтинг
        if (user.getFilter().getRating() > 0.0)
        {
            double rating = user.getFilter().getRating();
            userList = userList.stream().filter(userObj ->
                    userObj.getCard().getRating() >= rating - MyProperties.RATING_FALSITY)
                    .collect(Collectors.toList());

            if (userList.size() == 0)
                return null;
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
        List<User> dislikesUsers = userList.stream()
                .filter(userObj-> user.getCard().getDisLikes().contains(userObj.getId())) // Отсортированы в базе
                .sorted(Comparator.comparingInt(u -> user.getCard().getDisLikes().indexOf(u.getId())))
                .collect(Collectors.toList());

        // убирает из списка пользоватлей, которых дизлайкали раньше
        userList.removeAll(dislikesUsers);

        if (userList.size() >= MyProperties.USERS_LIST_SIZE)
            userList.subList(MyProperties.USERS_LIST_SIZE, userList.size()).clear();

        // Добиваем пачку пользователями, которых уже дизлайкали
        for (int i = 0; i < dislikesUsers.size() && userList.size() < MyProperties.USERS_LIST_SIZE; i++) {
            userList.add(dislikesUsers.get(i));
        }
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
