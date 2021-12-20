package usecase;

import domain.entity.model.OnlineStatus;
import usecase.port.UserRepository;

import java.util.List;

public class GetUserFields {

    private final UserRepository userRepository;

    public GetUserFields(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<OnlineStatus> getStatusByIds(Integer[] ids) {
        return userRepository.getOnlineStatusByIds(ids);
    }
}
