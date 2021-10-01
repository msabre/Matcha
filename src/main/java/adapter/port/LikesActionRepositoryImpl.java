package adapter.port;

import adapter.port.model.DBConfiguration;
import domain.entity.model.types.Action;
import usecase.port.LikesActionRepository;

import java.sql.*;
import java.util.Collections;
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
        putUpdateAction(from, to, Action.MATCH);
        insertLike(to, from);
    }

    private void insertLike(int from, int to) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword())) {
            boolean putAlready;
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM matcha.LIKES_ACTION WHERE FROM_USR = ?")) {
                statement.setInt(1, from);
                statement.execute();

                ResultSet resultSet = statement.getResultSet();
                putAlready = resultSet.next();
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

    public List<Integer> getMatchUserIds(int id) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM matcha.LIKES_ACTION acts WHERE (acts.FROM_USR = ? OR acts.TO_USR = ?) AND ACTION = ?")) {
            statement.setInt(1, id);
            statement.setInt(2, id);
            statement.setString(3, Action.MATCH.getValue());
            statement.execute();

            try (ResultSet rs = statement.getResultSet()) {
                List<Integer> ids = new LinkedList<>();
                while (rs.next()) {
                    int whoId = rs.getInt("TO_USR");
                    if (whoId == id)
                        whoId = rs.getInt("FROM_USR");
                    ids.add(whoId);
                }
                return ids;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
