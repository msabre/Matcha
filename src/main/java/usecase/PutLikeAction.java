package usecase;

import adapter.port.model.RatingChangesDefaultValue;
import config.MyProperties;
import domain.entity.LikeAction;
import usecase.port.LikesActionRepository;
import usecase.port.UserCardRepository;
import usecase.port.UserRepository;

public class PutLikeAction {
    LikesActionRepository likesActionRepository;
    UserCardRepository userCardRepository;
    UserRepository userRepository;

    public PutLikeAction(LikesActionRepository likesActionRepository, UserCardRepository userCardRepository, UserRepository userRepository) {
        this.likesActionRepository = likesActionRepository;
        this.userCardRepository = userCardRepository;
        this.userRepository = userRepository;
    }

    public boolean putMatchOrLike(LikeAction likeAction) {
        int to = likeAction.getToUsr();
        int from = likeAction.getFromUsr();

        boolean isMatch = likesActionRepository.checkLike(to, from);
        if (isMatch) {
            likesActionRepository.match(from, to);
            userCardRepository.increaseRating(from, RatingChangesDefaultValue.INCREASE_MATCH);
            userCardRepository.increaseRating(to, RatingChangesDefaultValue.INCREASE_MATCH);
        } else {
            likesActionRepository.like(from, to);
            userCardRepository.increaseRating(to, RatingChangesDefaultValue.INCREASE_LIKE);
        }
        return isMatch;
    }

    public void disLike(LikeAction likeAction) {
        likesActionRepository.dislike(likeAction.getFromUsr(), likeAction.getToUsr());
        userCardRepository.decreaseRating(likeAction.getToUsr(), RatingChangesDefaultValue.DECREASE_DISLIKE);
    }

    public void fixVisit(LikeAction likeAction) {
        likesActionRepository.fixVisit(likeAction.getFromUsr(), likeAction.getToUsr());
    }

    public void block(LikeAction likeAction) {
        likesActionRepository.block(likeAction.getFromUsr(), likeAction.getToUsr());
        userCardRepository.decreaseRating(likeAction.getToUsr(), RatingChangesDefaultValue.DECREASE_BLOCK);
    }

    public boolean fake(LikeAction likeAction) {
        int toUsr = likeAction.getToUsr();
        likesActionRepository.fake(likeAction.getFromUsr(), toUsr);
        userCardRepository.decreaseRating(toUsr, RatingChangesDefaultValue.DECREASE_FAKE);

        if (userRepository.fakeIncrease(toUsr) >= MyProperties.FAKE_COUNT) {
            userRepository.banById(toUsr);
            return true;
        }
        return false;
    }

    public void takeFake(LikeAction likeAction) {
        likesActionRepository.takeFake(likeAction.getFromUsr(), likeAction.getToUsr());
        userRepository.fakeDecrease(likeAction.getToUsr());
        userCardRepository.increaseRating(likeAction.getToUsr(), RatingChangesDefaultValue.INCREASE_TAKE_FAKE);
    }

    public void deleteLike(LikeAction likeAction) {
        likesActionRepository.deleteLike(likeAction.getFromUsr(), likeAction.getToUsr());
        userCardRepository.decreaseRating(likeAction.getToUsr(), RatingChangesDefaultValue.DECREASE_TAKE_LIKE);
    }
}
