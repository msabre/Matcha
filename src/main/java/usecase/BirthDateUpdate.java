package usecase;

import usecase.port.UserRepository;

import java.util.Date;

public class BirthDateUpdate {
    UserRepository userRepository;

    public void update(int userId, Date birthDate, int yearsOld) {
        userRepository.birthDateUpdate(userId, birthDate, yearsOld);
    }

    public BirthDateUpdate(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
