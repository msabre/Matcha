package usergenerator;

import config.MyConfiguration;
import domain.entity.User;
import domain.entity.UserCard;
import domain.entity.model.types.GenderType;
import domain.entity.model.types.SexualPreferenceType;
import usecase.port.ChatAffiliationRepository;
import usecase.port.MessageRepository;
import usecase.port.UserCardRepository;
import usecase.port.UserRepository;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Generator {

    private final static String DIALOG_DELIMITER = "<---------*********--------->";
    private final static String TOPIC_DELIMITER = "--&&&";

    private final UserRepository userRepository;
    private final UserCardRepository userCardRepository;
    private final MessageRepository messageRepository;
    private final ChatAffiliationRepository chatAffiliationRepository;
    private final List<User> userList;

    public Generator() {
        userRepository = MyConfiguration.userRepository();
        userCardRepository = MyConfiguration.userCardRepository();
        messageRepository = MyConfiguration.messageRepository();
        chatAffiliationRepository = MyConfiguration.chatAffiliationRepository();
        userList = new ArrayList<>();
    }

    public void generate(String male) throws URISyntaxException {
        List<String> cityList = readFile(Paths.get(Generator.class.getResource("/generator/cityList.txt").toURI()).toFile().getPath());
        List<String> interestsList = readFile(Paths.get(Generator.class.getResource("/generator/interestsList.txt").toURI()).toFile().getPath());

        List<String> namesList;
        List<String> sexualPreferenceList;
        switch (male) {
            case "male":
                namesList = readFile(Paths.get(Generator.class.getResource("/generator/namesListMale.txt").toURI()).toFile().getPath());
                sexualPreferenceList = readFile(Paths.get(Generator.class.getResource("/generator/sexualPreferenseMale.txt").toURI()).toFile().getPath());
                break;
            case "female":
                namesList = readFile(Paths.get(Generator.class.getResource("/generator/namesListFemale.txt").toURI()).toFile().getPath());
                sexualPreferenceList = readFile(Paths.get(Generator.class.getResource("/generator/sexualPreferenseFemale.txt").toURI()).toFile().getPath());
                break;
            default:
                return ;
        }

        if (namesList == null || cityList == null || sexualPreferenceList == null || interestsList == null)
            return;

        for (String name : namesList) {
            User user = new User();

            String[] fio = name.split(" ");
            user.setLastName(fio[0]);
            user.setFirstName(fio[1]);
            user.setMiddleName(fio[2]);
            user.setLocation(getOne(cityList));
            user.setYearsOld(getIntOfRange(18, 45));

            UserCard card = new UserCard();
            card.setSexualPreference(SexualPreferenceType.fromStr(getOne(sexualPreferenceList)));
            card.setGender(GenderType.fromStr(male));

            user.setCard(card);

            userRepository.save(user);

            List<String> tags = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                int done = 0;
                while (done < 1) {
                    String tag = getOne(interestsList);
                    if (!tags.contains(tag)) {
                        tags.add(tag);
                        done = 1;
                    }
                }
            }
            card.setTags(tags);
            card.setRating(getDoubleOfRange(1.0, 4.7));

            userCardRepository.save(card);
            userList.add(user);
        }
    }

    private void generateMessageHistoryForAllChatFreeUsers(int dialogCount) throws URISyntaxException {
        List<String> dialogsLines = readFile(Paths.get(Generator.class.getResource("/generator/dialogs.txt").toURI()).toFile().getPath());
        if (dialogsLines == null)
            return;

        List<Integer> dialogIndexes = dialogsLines.stream().filter(line -> line.equals(DIALOG_DELIMITER)).map(dialogsLines::indexOf).collect(Collectors.toList());
        List<List<String>> mess = new ArrayList<>();
        for (int i = 0; i < (dialogIndexes.size() / 2); i++) {
            mess.add(dialogsLines.subList(dialogIndexes.get(i), dialogIndexes.get(i + 1)));
        }
        

        List<Integer> freeChatUsersIds = userList.stream().map(User::getId).sorted(Integer::compareTo).collect(Collectors.toList());
        List<Integer> userIds = new ArrayList<>(freeChatUsersIds);
        int maxUserId = freeChatUsersIds.get(freeChatUsersIds.size() - 1);
        int maxChatId = chatAffiliationRepository.getChatMaxId() + 1;

        for (int index = 0; index < userIds.size(); index++) {

            int counter = 0;
            while (counter < dialogCount) {
                int userId = freeChatUsersIds.get(index);
                int toUsr = getIntOfRange(userId, maxUserId);

                Date creationTime = new Date();
                chatAffiliationRepository.create(userId, toUsr, ++maxChatId);
                for (List<String> dialog : mess) {
                    for (String message : dialog) {
                        if (message.equals(TOPIC_DELIMITER)) {
                            // TODO уменьшить дату
                            continue;
                        }
                    }
                }
                
                freeChatUsersIds.remove(toUsr);
                counter++;
            }
        }
    }
    
    private int getIntOfRange(int by, int to) {
        return (int) (by + Math.random() * (to - by));
    }

    private double getDoubleOfRange(double by, double to) {
        return (by + Math.random() * (to - by));
    }

    private String getOne(List<String> lst) {
        int integer = (int) (Math.random() * (double) lst.size());

        return lst.get(integer);
    }

    private List<String> readFile(String path) {
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader reader = new BufferedReader(fileReader);

            List<String> listLines = new ArrayList<>();
            while (reader.ready()) {
                listLines.add(reader.readLine());
            }

            return listLines;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws URISyntaxException {
        Generator generator = new Generator();

        generator.generate("male");
        generator.generate("female");

        
    }
}
