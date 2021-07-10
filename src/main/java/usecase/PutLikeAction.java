package usecase;

import usecase.port.LikesActionRepository;
import usecase.port.UserRepository;

public class PutLikeAction {
    LikesActionRepository likesActionRepository;
    UserRepository userRepository;

    public PutLikeAction(LikesActionRepository likesActionRepository, UserRepository userRepository) {
        this.likesActionRepository = likesActionRepository;
        this.userRepository = userRepository;
    }

    public void like(int from, int to) {
        likesActionRepository.like(from, to);
    }

    public void dislike(int from, int to) {
        likesActionRepository.dislike(from, to);
    }

    public void match(int from, int to) {
        likesActionRepository.match(from, to);
        userRepository.createChatBetweenTwoUsers(from, to);
    }
}
