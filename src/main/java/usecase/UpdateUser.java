package usecase;

import domain.entity.model.OnlineStatus;
import usecase.port.UserRepository;

import java.time.ZoneId;

public class UpdateUser {

    UserRepository userRepository;

    public void email(int userId, String email) {
        userRepository.updateEmail(userId, email);
    }

    public void status(int userId, ZoneId zoneId, OnlineStatus.Status status) {
        userRepository.updateStatus(userId, zoneId, status);
    }

    public UpdateUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
