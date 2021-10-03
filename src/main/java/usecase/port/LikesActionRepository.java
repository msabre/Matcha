package usecase.port;

import java.util.List;

public interface LikesActionRepository {

    boolean checkLike(int from, int to);

    void like(int from, int to);

    void dislike(int from, int to);

    void match(int from, int to);

    void putDislikeForUsers(int from, List<Integer> ids, List<Integer> dislikesAlready);

    void deleteLike(int from, int to);

    List<Integer> getNMatchUserIds(int id, int size);

    List<Integer> getNMatchUserIdsAfterSpecificId(int id, int specificId, int size);
}
