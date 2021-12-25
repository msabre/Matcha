package usecase.port;

import domain.entity.LikeAction;
import domain.entity.model.types.Action;

import java.util.List;

public interface LikesActionRepository {

    boolean checkLike(int from, int to);

    void like(int from, int to);

    void dislike(int from, int to);
    
    void fixVisit(int from, int to);

    void block(int from, int to);

    void fake(int from, int to);

    void takeFake(int from, int to);

    void deleteAction(int from, int to, String action);
    
    void match(int from, int to);

    void putDislikeForUsers(int from, List<Integer> ids, List<Integer> dislikesAlready);

    void deleteLike(int from, int to);

    List<Integer> getToUserDislikesByIds(int from, List<Integer> ids);

    List<LikeAction> getNFrom(Action action, int id, int size);

    List<LikeAction> getNFromAfterId(Action action, int id, int specificId, int size);

    List<LikeAction> getNTo(Action action, int to, int size);

    List<LikeAction> getNToAfterId(Action action, int to, int specificId, int size);

    List<LikeAction> getByFromUsrOrToUsrAndAction(int id, String action);

    List<LikeAction> getNLikeForUserId(int id, int size); // генератор
}
