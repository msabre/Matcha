package usergenerator.part;

import adapter.controller.UserController;
import config.MyConfiguration;
import domain.entity.FilterParams;
import domain.entity.User;
import usecase.port.LikesActionRepository;
import usergenerator.UserGenerator;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

public class LikeGenerator extends Generator {
    
    private final LikesActionRepository likesActionRepository;
    private UserController userController;
    
    public LikeGenerator() {
        likesActionRepository = MyConfiguration.likesActionRepository();
        userController = MyConfiguration.userController();
    }

    public int generateNMatchForUser(int userId, int maxLikeCount) throws URISyntaxException {
        User user = userController.findUser(userId);
        if (user == null)
            return 0;

        FilterParams filterParams = user.getFilter();
        filterParams.setCommonTagsCount(0);
        filterParams.setRating(0.0);
        filterParams.setAgeBy(18);
        filterParams.setAgeTo(100);
        
        user.setFilter(filterParams);

        int likeCount = 0;
        List<String> cities = readFile(Paths.get(UserGenerator.class.getResource("/generator/cityList.txt").toURI()).toFile().getPath());
        cities.remove(user.getLocation());
        cities.set(0, user.getLocation());
        
        for (String city : cities) {
            user.getFilter().setLocation(city);
            System.out.println("Ставим лайки по городу: " + user.getFilter().getLocation());
            likeCount += likeUsers(user, userId, maxLikeCount, likeCount);

            if (likeCount >= maxLikeCount)
                break;
            System.out.println();
        }

        if (likeCount == 0)
            System.out.println("Не найдены пользователи, которых можно лайкнуть");

        return likeCount;
    }

    private int likeUsers(User user, int userId, int maxLikeCount, int currentLikeCount) {
        List<User> recommendedList = userController.getRecommendUsersList(user, maxLikeCount);
        if (recommendedList.isEmpty())
            return 0;

        int newLikeCount = 0;
        for (User toUsr : recommendedList) {
            if (newLikeCount + currentLikeCount >= maxLikeCount)
                break;

            likesActionRepository.like(userId, toUsr.getId());
            String fio = String.format("%s %s %s", toUsr.getLastName(), toUsr.getFirstName(), toUsr.getMiddleName());
            System.out.println("Поставили лайк пользователю, id: " + toUsr.getId() + ", ФИО: " + fio);
            newLikeCount++;
        }
        return newLikeCount;
    }

    public static void main(String[] args) throws URISyntaxException {
        LikeGenerator likeGenerator = new LikeGenerator();
        int likeCount = likeGenerator.generateNMatchForUser(182, 300);

        System.out.println();
        System.out.println("Общее количество поставленных лайков: " + likeCount);
    }
}
