package usergenerator;

import config.MyConfiguration;
import domain.entity.User;
import domain.entity.UserCard;
import domain.entity.types.GenderType;
import domain.entity.types.SexualPreferenceType;
import usecase.port.UserCardRepository;
import usecase.port.UserRepository;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Generator {

    private final UserRepository userRepository;
    private final UserCardRepository userCardRepository;

    private List<String> namesList;
    private List<String> cityList;
    private List<String> interestsList;
    private List<String> sexualPrefeneceList;

    public Generator() {
        userRepository = MyConfiguration.userRepository();
        userCardRepository = MyConfiguration.userCardRepository();
    }

    public void generate(String male) throws URISyntaxException {
        cityList = readFile(Paths.get(Generator.class.getResource("/generator/cityList.txt").toURI()).toFile().getPath());
        interestsList = readFile(Paths.get(Generator.class.getResource("/generator/interestsList.txt").toURI()).toFile().getPath());

        switch (male) {
            case "male":
                namesList = readFile(Paths.get(Generator.class.getResource("/generator/namesListMale.txt").toURI()).toFile().getPath());
                sexualPrefeneceList = readFile(Paths.get(Generator.class.getResource("/generator/sexualPreferenseMale.txt").toURI()).toFile().getPath());
                break;
            case "female":
                namesList = readFile(Paths.get(Generator.class.getResource("/generator/namesListFemale.txt").toURI()).toFile().getPath());
                sexualPrefeneceList = readFile(Paths.get(Generator.class.getResource("/generator/sexualPreferenseFemale.txt").toURI()).toFile().getPath());
                break;
            default:
                return ;
        }

        for (String name : namesList) {
            User user = new User();

            String[] fio = name.split(" ");
            user.setLastName(fio[0]);
            user.setFirstName(fio[1]);
            user.setMiddleName(fio[2]);
            user.setLocation(getOne(cityList));
            user.setYearsOld(getIntOfRange(18, 45));

            UserCard card = new UserCard();
            card.setSexualPreference(SexualPreferenceType.fromStr(getOne(sexualPrefeneceList)));
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
