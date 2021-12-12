package usergenerator.part;

import config.MyConfiguration;
import domain.entity.LikeAction;
import domain.entity.Message;
import domain.entity.model.types.Action;
import domain.entity.model.types.MessageStatus;
import domain.entity.model.types.MessageType;
import usecase.port.ChatAffiliationRepository;
import usecase.port.LikesActionRepository;
import usecase.port.MessageRepository;
import usecase.port.UserRepository;
import usergenerator.UserGenerator;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MessageGenerator extends Generator {

    private final static String DIALOG_DELIMITER = "<---------*********--------->";
    private final static String TOPIC_DELIMITER = "--&&&";

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChatAffiliationRepository chatAffiliationRepository;
    private final LikesActionRepository likesActionRepository;
    private List<List<String>> dialogs;

    public MessageGenerator() {
        this.userRepository = MyConfiguration.userRepository();
        this.messageRepository = MyConfiguration.messageRepository();
        this.chatAffiliationRepository = MyConfiguration.chatAffiliationRepository();
        this.likesActionRepository = MyConfiguration.likesActionRepository();
    }

    public int generateNChatForUser(int userId, int dialogCount, int currentCount, int lastMatchId) throws URISyntaxException {
        String haveMatchWithUser;
        List<Integer> matches;
        if (currentCount == 0)
            matches = likesActionRepository.getNActionForUserId(Action.MATCH, userId, dialogCount).stream().map(LikeAction::getToUsr).collect(Collectors.toList());
        else
            matches = likesActionRepository.getNActionsUserIdsAfterSpecificId(Action.MATCH, userId, lastMatchId,  dialogCount).stream().map(LikeAction::getToUsr).collect(Collectors.toList());
        
        if (matches.size() == 0) // Стоп рекурсии
            return currentCount;

        lastMatchId = matches.get(matches.size() - 1);
        haveMatchWithUser = matches.stream().map(String::valueOf).collect(Collectors.joining(","));

        List<Integer> freeChatUsersIds = userRepository.getNUserIdsWithFreeChatByIds(haveMatchWithUser, dialogCount);
        freeChatUsersIds.remove(new Integer(userId));

        int maxChatId = chatAffiliationRepository.getChatMaxId();

        int dialogIndex = 0;
        for (Integer toUsr: freeChatUsersIds) {
            System.out.println(String.format("Генерирую диалог [%s], с пользователем id: [%s]", maxChatId + 1, toUsr));
            
            List<String> dialog = dialogs.get(dialogIndex++);
            if (dialogIndex == dialogs.size())
                dialogIndex = 0;

            Date creationTime = new Date(new Date().getTime() - (long) 60 * 60 * 24 * 365 * 1000); // минус год
            chatAffiliationRepository.create(userId, toUsr, ++maxChatId);

            for (String content : dialog) {
                if (content.equals(TOPIC_DELIMITER)) {
                    creationTime = new Date(creationTime.getTime() + (long) 60 * 60 * 24 * getIntOfRange(1, 3) * 1000); // плюс 2 дня
                    continue;
                }
                Message messageType = new Message();
                messageType.setStatus(MessageStatus.DELIVERED);
                messageType.setType(MessageType.TEXT);
                messageType.setFromId(userId);
                messageType.setToId(toUsr);
                messageType.setCreationTime(creationTime);
                messageType.setChatId(maxChatId);
                messageType.setContent(content);
                messageRepository.save(messageType);

                creationTime = new Date(new Date().getTime() + getIntOfRange(1, 30) * 1000);
            }
            currentCount++;
        }

        if (currentCount < dialogCount)
            generateNChatForUser(userId, dialogCount, currentCount, lastMatchId);

        return currentCount;
    }

    public void readDialogsFromFile() throws URISyntaxException {
        List<String> dialogsLines = readFile(Paths.get(UserGenerator.class.getResource("/generator/dialogs.txt").toURI()).toFile().getPath());
        if (dialogsLines == null)
            return;

        List<Integer> dialogIndexes = IntStream.range(0, dialogsLines.size()).filter(i -> dialogsLines.get(i).equals(DIALOG_DELIMITER)).boxed().collect(Collectors.toList());
        dialogs = new ArrayList<>();
        for (int i = 0; i < dialogIndexes.size() / 2; i++) {
            dialogs.add(dialogsLines.subList(dialogIndexes.get(i) + 1, dialogIndexes.get(i + 1)));
        }
    }

    public static void main(String[] args) throws URISyntaxException {

        MessageGenerator messageGenerator = new MessageGenerator();
        messageGenerator.readDialogsFromFile();
        messageGenerator.generateNChatForUser(182, 67, 0, 0);
    }
}
