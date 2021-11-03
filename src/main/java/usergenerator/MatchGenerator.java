package usergenerator;

import config.MyConfiguration;
import domain.entity.LikeAction;
import domain.entity.Message;
import domain.entity.model.types.MessageStatus;
import domain.entity.model.types.MessageType;
import usecase.port.ChatAffiliationRepository;
import usecase.port.LikesActionRepository;
import usecase.port.MessageRepository;
import usecase.port.UserRepository;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MatchGenerator extends Generator {
    
    private final LikesActionRepository likesActionRepository;

    public MatchGenerator() {
        likesActionRepository = MyConfiguration.likesActionRepository();
    }

    private void generateNMatchForUser(int userId, int maxMatchCount) {
        List<LikeAction> userLikes = likesActionRepository.getNLikeForUserId(userId, maxMatchCount);
        for (LikeAction like : userLikes) {
            likesActionRepository.match(like.getToUsr(), userId);
        }
    }

    public static void main(String[] args) {
        MatchGenerator messageGenerator = new MatchGenerator();
        messageGenerator.generateNMatchForUser(182, 10);
    }
}
