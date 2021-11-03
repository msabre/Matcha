package usergenerator;

import config.MyConfiguration;
import domain.entity.Message;
import domain.entity.User;
import domain.entity.UserCard;
import domain.entity.model.types.GenderType;
import domain.entity.model.types.MessageStatus;
import domain.entity.model.types.MessageType;
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
import java.util.stream.IntStream;

public class UserGenerator extends Generator {

    private final UserRepository userRepository;
    private final UserCardRepository userCardRepository;

    public UserGenerator() {
        userRepository = MyConfiguration.userRepository();
        userCardRepository = MyConfiguration.userCardRepository();
    }

    public void generate(String male) throws URISyntaxException {
        List<String> cityList = readFile(Paths.get(UserGenerator.class.getResource("/generator/cityList.txt").toURI()).toFile().getPath());
        List<String> interestsList = readFile(Paths.get(UserGenerator.class.getResource("/generator/interestsList.txt").toURI()).toFile().getPath());

        List<String> namesList;
        List<String> sexualPreferenceList;
        switch (male) {
            case "male":
                namesList = readFile(Paths.get(UserGenerator.class.getResource("/generator/namesListMale.txt").toURI()).toFile().getPath());
                sexualPreferenceList = readFile(Paths.get(UserGenerator.class.getResource("/generator/sexualPreferenseMale.txt").toURI()).toFile().getPath());
                break;
            case "female":
                namesList = readFile(Paths.get(UserGenerator.class.getResource("/generator/namesListFemale.txt").toURI()).toFile().getPath());
                sexualPreferenceList = readFile(Paths.get(UserGenerator.class.getResource("/generator/sexualPreferenseFemale.txt").toURI()).toFile().getPath());
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
            user.setLocation((String) getOne(cityList));
            user.setYearsOld(getIntOfRange(18, 45));

            UserCard card = new UserCard();
            card.setSexualPreference(SexualPreferenceType.fromStr((String) getOne(sexualPreferenceList)));
            card.setGender(GenderType.fromStr(male));

            user.setCard(card);

            user.setId(userRepository.save(user));

            List<String> tags = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                int done = 0;
                while (done < 1) {
                    String tag = (String) getOne(interestsList);
                    if (!tags.contains(tag)) {
                        tags.add(tag);
                        done = 1;
                    }
                }
            }
            card.setTags(tags);
            card.setRating(getDoubleOfRange(1.0, 4.7));

            userCardRepository.save(card);
        }
    }

    public static void main(String[] args) throws URISyntaxException {
        UserGenerator userGenerator = new UserGenerator();

        userGenerator.generate("male");
        userGenerator.generate("female");
    }
}
