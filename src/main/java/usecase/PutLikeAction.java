package usecase;

import adapter.port.model.RatingChangesDefaultValue;
import usecase.port.ChatAffiliationRepository;
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

    public boolean putMatchOrLike(int from, int to) {
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

    public void deleteLike(int from, int to) {
        likesActionRepository.deleteLike(from, to);
        userCardRepository.decreaseRating(to, RatingChangesDefaultValue.DECREASE_TAKE_LIKE);
    }
}
