package usecase;

import usecase.port.LikesActionRepository;

public class PutLikeAction {
    LikesActionRepository likesActionRepository;

    public PutLikeAction(LikesActionRepository likesActionRepository) {
        this.likesActionRepository = likesActionRepository;
    }

    public void like(int from, int to) {
        likesActionRepository.like(from, to);
    }

    public void dislike(int from, int to) {
        likesActionRepository.dislike(from, to);
    }

    public void match(int from, int to) {
        likesActionRepository.match(from, to);
    }
}
