package usecase;

import domain.entity.Photo;
import usecase.port.UserCardRepository;

import java.util.List;

public class UpdatePhotoParams {

    UserCardRepository userCardRepository;

    public void update(int userId, List<Photo> photoList) {
        userCardRepository.updatePhotosParams(userId, photoList);
    }

    public UpdatePhotoParams(UserCardRepository userCardRepository) {
        this.userCardRepository = userCardRepository;
    }
}
