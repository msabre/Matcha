package usecase;

import usecase.port.UserRepository;

public class UpdateEmail {

    UserRepository userRepository;

    public void update(int userId, String email) {
        userRepository.updateEmail(userId, email);
    }

    public UpdateEmail(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
