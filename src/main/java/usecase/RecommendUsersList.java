package usecase;

import config.MyProperties;
import domain.entity.User;

import usecase.port.LikesActionRepository;
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

    public List<User> get(User user) {
        this.user = user;
        List<User> userList = userRepository.getAllUserInSameLocation(user.getLocation());

        userList = mandatoryFilter(userList);
        if (userList == null)
            return null;

        userList = customFilter(userList);
        if (userList == null)
            return null;

        sortUserList(userList);
        fixSize(userList);

        // ставим матч если кто то из пользователей уже лайкнул клиента
        userList.forEach((userObj) -> {
            if (userObj.getCard().getLikes().contains(user.getId()))
                userObj.setMatch(true);
        });

        List<Integer> ids = new ArrayList<>(userList.size());
        userList.forEach(userObj -> ids.add(userObj.getId()));

        // постаивли всем пользователям дизлайк
        likesActionRepository.putDislikeForUsers(user.getId(), ids);

        return userList;
    }

    private List<User> mandatoryFilter(List<User> userList) {
        if (userList == null)
            return null;

        // Фильтруем по ориентации
        List<String> avalibalePreference = sexualConformity
                .get(user.getCard().getGender() + ";" + user.getCard().getSexual_preference());

        userList = userList.stream().filter((userObj) -> {
            String preferences = userObj.getCard().getGender() + ";" + userObj.getCard().getSexual_preference();
            return avalibalePreference.contains(preferences);

        }).collect(Collectors.toList());

        if (userList.size() == 0)
            return null;

        // Фильтруем тех кого уже лайкали
        List<Integer> likeIds = user.getCard().getLikes();
        userList = userList.stream().filter(userObj ->
                !likeIds.contains(userObj.getId())).collect(Collectors.toList());

        if (userList.size() == 0)
            return null;

        return userList;
    }

    private List<User> customFilter(List<User> userList) {

        // Фильтр по возрасту (обязательный)
        userList = userList.stream().filter(userObj ->
                userObj.getCard().getYearsOld() >= user.getFilter().getAgeBy() &&
                        userObj.getCard().getYearsOld() <= user.getFilter().getAgeTo())
                .collect(Collectors.toList());

        if (userList.size() == 0)
            return null;

        // Фильтр на количетсво общих интересов
        if (user.getFilter().getCommonTagsCount() != null)
        {
            int countSameTags = user.getFilter().getCommonTagsCount();
            userList = userList.stream()
                    .filter(userObj -> getCommonTagsCount(userObj) >= countSameTags)
                    .collect(Collectors.toList());

            if (userList.size() == 0)
                return null;
        }

        // Фильтр на рейтинг
        if (user.getFilter().getRating() != null)
        {
            double rating = user.getCard().getRating();
            userList = userList.stream().filter(userObj ->
                    userObj.getCard().getRating() >= rating - MyProperties.RATING_FALSITY &&
                            userObj.getCard().getRating() <= rating + MyProperties.RATING_FALSITY)
                    .collect(Collectors.toList());

            if (userList.size() == 0)
                return null;
        }

        return userList;
    }

    private void sortUserList(List<User> userList) {
        userList.sort((o1, o2) -> {
            int res = o1.getCard().getYearsOld() - o2.getCard().getYearsOld();
            if (res != 0)
                return res;

            double res2 = o1.getCard().getRating() - o2.getCard().getRating();
            if (res2 != 0)
                return res;

            return getCommonTagsCount(o1) - getCommonTagsCount(o2);
        });
    }

    // убирает из списка пользоватлей, которых дизлайкали раньше
    private void fixSize(List<User> userList) {
        List<Integer> dislikeIds = user.getCard().getDislikes();

        List<User> dislikesUsers = userList.stream()
                .filter(userObj-> dislikeIds
                .contains(userObj.getId()))
                .sorted(Comparator
                        .comparingInt(usr -> dislikeIds.indexOf(usr.getId())))
                .collect(Collectors.toList());

        userList.removeAll(dislikesUsers);

        if (userList.size() > MyProperties.COUNT_RECCOMENDED_USERS_LIST_SIZE)
            userList = userList.subList(0, MyProperties.COUNT_RECCOMENDED_USERS_LIST_SIZE);

        for (int i = 0; i < dislikesUsers.size()
                && userList.size() < MyProperties.COUNT_RECCOMENDED_USERS_LIST_SIZE; i++) {
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
