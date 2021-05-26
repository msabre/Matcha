package usecase.port;

import java.util.List;

public interface LikesActionRepository {
    List<Integer> getUserLikes(Integer userId);

    List<Integer> getUserDislikes(Integer userId);

    boolean isLike(int from, int to);

    void like(int from, int to);

    void dislike(int from, int to);

    void match(int from, int to);

    void putDislikeForUsers(int from, List<Integer> ids);
}
