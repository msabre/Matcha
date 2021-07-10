package usergenerator;

import config.MyConfiguration;
import domain.entity.User;
import domain.entity.UserCard;
import domain.entity.model.types.GenderType;
import domain.entity.model.types.SexualPreferenceType;
import usecase.port.UserCardRepository;
import usecase.port.UserRepository;

import java.io.*;
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

    public void generate(String male) {

        cityList = readFile(Generator.class.getResource("/generator/cityList.txt").getPath());
        interestsList = readFile(Generator.class.getResource("/generator/interestsList.txt").getPath());

        switch (male) {
            case "man":
                namesList = readFile(Generator.class.getResource("/generator/namesListMale.txt").getPath());
                sexualPrefeneceList = readFile(Generator.class.getResource("/generator/sexualPreferenseMale.txt").getPath());
                break;
            case "woman":
                namesList = readFile(Generator.class.getResource("/generator/namesListFemale.txt").getPath());
                sexualPrefeneceList = readFile(Generator.class.getResource("/generator/sexualPreferenseFemale.txt").getPath());
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

            int id = userRepository.save(user);

            UserCard card = new UserCard();
            card.setId(user.getCard().getId());
            card.setUserId(id);

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
            card.setYearsOld(getIntOfRange(18, 45));
            card.setSexualPreference(SexualPreferenceType.valueOf(getOne(sexualPrefeneceList)));
            card.setGender(GenderType.valueOf(male));


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

    public static void main(String[] args) {
        Generator generator = new Generator();
        generator.generate("man");
        generator.generate("woman");
    }
}
