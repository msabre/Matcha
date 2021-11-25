package usergenerator.part;

import config.MyConfiguration;
import domain.entity.LikeAction;
import usecase.port.LikesActionRepository;

import java.util.List;

public class MatchGenerator extends Generator {

    private final LikesActionRepository likesActionRepository;

    public MatchGenerator() {
        likesActionRepository = MyConfiguration.likesActionRepository();
    }

    public int generateNMatchForUser(int userId, int maxMatchCount) {
        int matchCount = 0;
        List<LikeAction> userLikes = likesActionRepository.getNLikeForUserId(userId, maxMatchCount);
        for (LikeAction like : userLikes) {
            likesActionRepository.match(like.getToUsr(), userId);
            System.out.println("Матч между пользвателями " + userId + " и " + like.getToUsr() + "!");
            matchCount++;
        }
        return matchCount;
    }

    public static void main(String[] args) {
        MatchGenerator messageGenerator = new MatchGenerator();
        int totalMatchCount = messageGenerator.generateNMatchForUser(182, 67);

        System.out.println();
        System.out.println("Всего Матчей поставлено: " + totalMatchCount);
    }
}
