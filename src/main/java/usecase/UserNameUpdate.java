package usecase;

import usecase.port.UserRepository;

public class UserNameUpdate {
    UserRepository userRepository;

    public void update(int userId, String username) {
        userRepository.updateUsername(userId, username);
    }

    public UserNameUpdate(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
