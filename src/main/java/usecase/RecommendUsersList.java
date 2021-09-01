package usecase;

import config.MyProperties;
import domain.entity.User;

import domain.entity.model.types.Action;
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
        List<User> userList = userRepository.getAllUserInSameLocation(user.getFilter().getLocation(), user.getId());

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
            Map<Integer, Action> actionMap = userObj.getCard().getActionMap();
            if (actionMap.containsKey(user.getId()) && actionMap.get(user.getId()).equals(Action.LIKE))
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

        userList = userList.stream().filter(userObj ->
                user.getCard().getActionMap().containsKey(userObj.getId()) &&
                        !user.getCard().getActionMap().get(userObj.getId()).equals(Action.LIKE) &&
                            !user.getCard().getActionMap().get(userObj.getId()).equals(Action.MATCH)
                )
                .collect(Collectors.toList());

        if (userList.size() == 0)
            return null;

        // Фильтруем по ориентации
        List<String> avalibalePreference = sexualConformity
                .get(user.getCard().getGender().getValue() + ";" + user.getCard().getSexualPreference().getValue());

        userList = userList.stream().filter((userObj) -> {
            String preferences = String.format("%s;%s", userObj.getCard().getGender().getValue(), userObj.getCard().getSexualPreference().getValue());
            return avalibalePreference.contains(preferences);

        }).collect(Collectors.toList());

        if (userList.size() == 0)
            return null;

        return userList;
    }

    private List<User> customFilter(List<User> userList) {

        // Фильтр по возрасту (обязательный)
        userList = userList.stream().filter(userObj ->
                userObj.getYearsOld() >= user.getFilter().getAgeBy() &&
                        userObj.getYearsOld() <= user.getFilter().getAgeTo())
                .collect(Collectors.toList());

        if (userList.size() == 0)
            return null;

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
        userList.sort((o1, o2) -> {
            int res = o1.getYearsOld() - o2.getYearsOld();
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
        List<Integer> dislikeIds = user.getCard().getActionMap().entrySet()
                .stream().filter(entry -> entry.getValue().equals(Action.DISLIKE))
                .map(Map.Entry::getKey).collect(Collectors.toList());

        List<User> dislikesUsers = userList.stream()
                .filter(userObj-> dislikeIds.contains(userObj.getId()))
                    .sorted(Comparator
                        .comparingInt(usr -> dislikeIds.indexOf(usr.getId())))
                .collect(Collectors.toList());

        userList.removeAll(dislikesUsers);

        if (userList.size() > MyProperties.USERS_LIST_SIZE)
            userList = userList.subList(0, MyProperties.USERS_LIST_SIZE);

        // Добиваем пачку пользователями, которых уже дизлайкали
        for (int i = 0; i < dislikesUsers.size()
                && userList.size() < MyProperties.USERS_LIST_SIZE; i++) {
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
