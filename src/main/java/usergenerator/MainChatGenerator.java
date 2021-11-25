package usergenerator;

import usergenerator.part.Generator;
import usergenerator.part.LikeGenerator;
import usergenerator.part.MatchGenerator;
import usergenerator.part.MessageGenerator;

import java.net.URISyntaxException;

public class MainChatGenerator extends Generator {
    
    public static void main(String[] args) throws URISyntaxException {
        int userId = 182;
        int maxChatCount = 100;
        
        LikeGenerator likeGenerator = new LikeGenerator();
        MatchGenerator matchGenerator = new MatchGenerator();
        MessageGenerator messageGenerator = new MessageGenerator();

        int countLikes = likeGenerator.generateNMatchForUser(userId, maxChatCount);
        System.out.println();
        System.out.println("Всего было поставлено лайков: " + countLikes);
        System.out.println();

        int matchCount = matchGenerator.generateNMatchForUser(userId, countLikes);
        System.out.println();
        System.out.println("Всего было поставлено матчей: " + matchCount);
        System.out.println();

        messageGenerator.readDialogsFromFile();
        int dialogsCount = messageGenerator.generateNChatForUser(userId, matchCount, 0, 0);
        System.out.println();
        System.out.println("Всего было сгенерированно чатов: " + dialogsCount);
        System.out.println();
    }
}
