package adapter.port;

import adapter.port.model.DBConfiguration;
import domain.entity.model.types.Action;
import usecase.port.LikesActionRepository;

import java.sql.*;
import java.util.List;

public class LikesActionRepositoryImpl implements LikesActionRepository {

    private static LikesActionRepositoryImpl instance;


    private final DBConfiguration config = DBConfiguration.getConfig();;

    private LikesActionRepositoryImpl() {
    }

    public static LikesActionRepositoryImpl getRepository() {
        if (instance == null) {
            instance = new LikesActionRepositoryImpl();
        }

        return instance;
    }

    @Override
    public void match(int from, int to) {
        putUpdateAction(from, to, Action.MATCH);
        removeLine(to, from);
    }

    @Override
    public void like(int from, int to) {
        putUpdateAction(from, to, Action.LIKE);
    }

    @Override
    public void dislike(int from, int to) {
        putUpdateAction(from, to, Action.DISLIKE);
    }

    private void putUpdateAction(int from, int to, Action action) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement putLikeAct = connection.prepareStatement("UPDATE matcha.LIKES_ACTION SET ACTION = ?, CREATION_TIME = NOW() WHERE FROM_USR = ? AND TO_USR = ?"))
        {
            putLikeAct.setString(1, action.toString());
            putLikeAct.setInt(2, from);
            putLikeAct.setInt(3, to);

            putLikeAct.execute();

        } catch (SQLException e) {
            System.err.println("Не удалось поставить пользователю с id=" + to + " " + action);
        }
    }

    public void removeLine(int from, int to) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement putLikeAct = connection.prepareStatement("DELETE FROM matcha.LIKES_ACTION WHERE FROM_USR = ? AND TO_USR = ?"))
        {
            putLikeAct.setInt(1, from);
            putLikeAct.setInt(2, to);

            putLikeAct.execute();

        } catch (SQLException e) {
            System.err.println("Не удалось удалить строку с действием от id=" + from + " к id:" + to);
        }
    }

    @Override
    public void putDislikeForUsers(int from, List<Integer> ids, List<Integer> dislikesAlready) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword()))
        {
            for (int id : ids)
            {
                if (!dislikesAlready.contains(id)) {
                    try (PreparedStatement insert = connection.prepareStatement(
                            "INSERT INTO matcha.LIKES_ACTION(FROM_USR, TO_USR, ACTION) VALUES(?, ?, 'DISLIKE')")) {
                        insert.setInt(1, from);
                        insert.setInt(2, id);

                        insert.execute();
                    } catch (SQLException e) {
                        System.err.println("Не удалось добавить дизлайк от пользователя с id: " + from +
                                " пользователю: " + id);
                    }
                }
                else {
                    try (PreparedStatement update = connection.prepareStatement("UPDATE matcha.LIKES_ACTION SET CREATION_TIME = NOW() WHERE FROM_USR = ? AND TO_USR = ?")) {
                        update.setInt(1, from);
                        update.setInt(2, id);

                        update.execute();
                    } catch (SQLException e) {
                        System.err.println("Не удалось обновить дизлайк от пользователя с id: " + from +
                                " пользователю: " + id);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
