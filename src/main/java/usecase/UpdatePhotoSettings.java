package usecase;

import usecase.port.UserCardRepository;

public class UpdatePhotoSettings {

    UserCardRepository userCardRepository;

    public void updateParams(int userId, String params) {
        userCardRepository.updatePhotosParams(userId, params);
    }

    public void updateMain(int userId, Integer mainPhoto) {
        userCardRepository.updateMainPhoto(userId, mainPhoto);
    }

    public UpdatePhotoSettings(UserCardRepository userCardRepository) {
        this.userCardRepository = userCardRepository;
    }
}
