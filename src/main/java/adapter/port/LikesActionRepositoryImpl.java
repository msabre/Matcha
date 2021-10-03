package adapter.port;

import adapter.port.model.DBConfiguration;
import domain.entity.LikeAction;
import domain.entity.model.types.Action;
import usecase.port.LikesActionRepository;

import java.sql.*;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
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
    public boolean checkLike(int from, int to) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement check = connection.prepareStatement("SELECT * FROM matcha.LIKES_ACTION acts WHERE acts.FROM_USR = ? AND acts.TO_USR = ? AND acts.ACTION = ?"))
        {
            int i = 1;
            check.setInt(i++, from);
            check.setInt(i++, to);
            check.setString(i, Action.LIKE.toString());

            check.execute();

            ResultSet resultSet = check.getResultSet();
            if (resultSet.next())
                return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void match(int from, int to) {
        updateOrInsertMatch(from, to);
        updateOrInsertMatch(to, from);
    }

    private void updateOrInsertMatch(int from, int to) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword())) {
            boolean putAlready;
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM matcha.LIKES_ACTION WHERE FROM_USR = ? AND TO_USR = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                statement.setInt(1, from);
                statement.setInt(2, to);
                statement.execute();

                ResultSet resultSet = statement.getResultSet();
                putAlready = resultSet.first();
            }

            int i = 1;
            if (!putAlready) {
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO matcha.LIKES_ACTION(FROM_USR, TO_USR, ACTION) VALUES(?, ?, ?)")) {
                    insert.setInt(i++, from);
                    insert.setInt(i++, to);
                    insert.setString(i, Action.MATCH.toString());

                    insert.execute();
                } catch (SQLException e) {
                    System.err.println("Не удалось добавить лайк от пользователя с id: " + from +
                            " пользователю: " + to);
                }
            }
            else {
                try (PreparedStatement update = connection.prepareStatement(
                        "UPDATE matcha.LIKES_ACTION acts SET CREATION_TIME = NOW(), acts.ACTION = ? WHERE acts.FROM_USR = ? AND acts.TO_USR = ?")) {
                    update.setString(i++, Action.MATCH.toString());
                    update.setInt(i++, from);
                    update.setInt(i, to);

                    update.execute();
                } catch (SQLException e) {
                    System.err.println("Не удалось обновить лайк от пользователя с id: " + from +
                            " пользователю: " + to);
                }
            }
        } catch (SQLException e) {
            System.err.println("Не удалось добавить лайк от пользователя с id: " + from +
                    " пользователю: " + to);
        }
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

    @Override
    public void deleteLike(int from, int to) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
            PreparedStatement putLikeAct = connection.prepareStatement(
                    "UPDATE matcha.LIKES_ACTION SET ACTION = 'DISLIKE', CREATION_TIME = NOW() WHERE FROM_USR = ? AND TO_USR = ?"))
        {
            putLikeAct.setInt(1, from);
            putLikeAct.setInt(2, to);
            putLikeAct.execute();

            putLikeAct.setInt(1, to);
            putLikeAct.setInt(2, from);
            putLikeAct.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<LikeAction> getNMatchUserIds(int id, int size) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM matcha.LIKES_ACTION acts WHERE acts.FROM_USR = ? AND ACTION = ? ORDER BY acts.CREATION_TIME DESC LIMIT ?")) {
            statement.setInt(1, id);
            statement.setString(2, Action.MATCH.getValue());
            statement.setInt(3, size);
            statement.execute();

            return getIds(statement, id);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public List<LikeAction> getNMatchUserIdsAfterSpecificId(int id, int specificId, int size) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM matcha.LIKES_ACTION acts WHERE acts.FROM_USR = ? " +
                     "AND acts.ACTION = ? AND acts.ID > ? ORDER BY acts.CREATION_TIME DESC LIMIT ?")) {
            statement.setInt(1, id);
            statement.setString(2, Action.MATCH.getValue());
            statement.setInt(3, specificId);
            statement.setInt(4, size);
            statement.execute();

            return getIds(statement, id);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private List<LikeAction> getIds(Statement statement, int id) throws SQLException {
        try (ResultSet rs = statement.getResultSet()) {
            List<LikeAction> ids = new LinkedList<>();
            while (rs.next()) {
                LikeAction likeAction = new LikeAction();
                likeAction.setId(rs.getInt("ID"));
                likeAction.setCreationTime(new Date(rs.getTimestamp("CREATION_TIME").getTime()));
                likeAction.setFromUsr(rs.getInt("FROM_USR"));
                likeAction.setToUsr(rs.getInt("TO_USR"));
                likeAction.setAction(Action.MATCH);
                ids.add(likeAction);
            }
            return ids;
        }
    }
}
