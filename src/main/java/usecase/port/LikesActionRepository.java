package usecase.port;

import domain.entity.LikeAction;

import java.util.List;

public interface LikesActionRepository {

    boolean checkLike(int from, int to);

    void like(int from, int to);

    void dislike(int from, int to);

    void match(int from, int to);

    void putDislikeForUsers(int from, List<Integer> ids, List<Integer> dislikesAlready);

    void deleteLike(int from, int to);

    List<Integer> getToUserDislikesByIds(int from, List<Integer> ids);

    List<LikeAction> getNMatchUserIds(int id, int size);

    List<LikeAction> getNMatchUserIdsAfterSpecificId(int id, int specificId, int size);
}
