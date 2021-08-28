package usecase;

import usecase.port.UserRepository;

public class FioUpdate {
    UserRepository userRepository;

    public void update(int userId, String[] fio) {
        userRepository.updateFio(userId, fio);
    }

    public FioUpdate(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
