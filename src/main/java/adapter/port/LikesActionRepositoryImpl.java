package adapter.port;

import adapter.port.model.DBConfiguration;
import domain.entity.model.types.Action;
import usecase.port.LikesActionRepository;

import java.sql.*;
import java.util.ArrayList;
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
    public List<Integer> getUserLikes(Integer userId) {
        return getUserLikesAction(userId, Action.LIKE);
    }

    @Override
    public List<Integer> getUserDislikes(Integer userId) {
        return getUserLikesAction(userId, Action.DISLIKE);
    }

    private List<Integer> getUserLikesAction(Integer userId, Action action) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM matcha.LIKES_ACTION WHERE FROM_USR = ? AND ACTION = ? " +
                     "ORDER BY CREATION_TIME"))
        {
            statement.setInt(1, userId);
            statement.setString(2, action.toString());

            statement.execute();

            ResultSet resultSet = statement.getResultSet();
            List<Integer> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(resultSet.getInt(2));
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void match(int from, int to) {
        putUpdateAction(from, to, Action.LIKE);
        putUpdateAction(to, from, Action.MATCH);
    }

    @Override
    public void like(int from, int to) {
        putUpdateAction(from, to, Action.LIKE);
    }

    @Override
    public void dislike(int from, int to) {
        putUpdateAction(from, to, Action.DISLIKE);
    }

    @Override
    public boolean isLike(int from, int to) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement isLikeFrom = connection.prepareStatement("SELECT * FROM matcha.LIKES_ACTION WHERE FROM_USR = ? AND TO_USR = ? AND ACTION = LIKE"))
        {
            isLikeFrom.setInt(1, from);
            isLikeFrom.setInt(2, to);
            isLikeFrom.execute();

            ResultSet rs1 = isLikeFrom.getResultSet();

            return rs1.next() && rs1.getInt(1) == from;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
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

    @Override
    public void putDislikeForUsers(int from, List<Integer> ids) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword())
            )
        {
            List<Integer> acts = getUserDislikes(from);

            for (int id : ids)
            {
                if (!acts.contains(id)) {
                    try (PreparedStatement insert = connection.prepareStatement("INSERT INTO matcha.LIKES_ACTION(FROM_USR, TO_USR, ACTION) " +
                            "VALUES(?, ?, 'DISLIKE')")) {
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
