package adapter.port;

import adapter.port.model.DBConfiguration;
import domain.entity.LikeAction;
import domain.entity.model.types.Action;
import usecase.port.LikesActionRepository;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

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
        updateByTypeListOrInsert(from, to, Action.MATCH, Collections.singletonList(Action.LIKE));
        updateByTypeListOrInsert(to, from, Action.MATCH, Collections.singletonList(Action.LIKE));
    }

    @Override
    public void like(int from, int to) {
        updateByTypeListOrInsert(from, to, Action.LIKE, Collections.singletonList(Action.DISLIKE));
    }

    @Override
    public void dislike(int from, int to) {
        updateByTypeListOrInsert(from, to, Action.DISLIKE, Arrays.asList(Action.LIKE, Action.MATCH));
    }

    @Override
    public void fixVisit(int from, int to) {
        updateByTypeListOrInsert(from, to, Action.VISIT, Collections.singletonList(Action.VISIT));
    }

    @Override
    public void block(int from, int to) {
        updateByTypeListOrInsert(from, to, Action.BLOCK, Arrays.asList(Action.LIKE, Action.DISLIKE, Action.MATCH));
    }

    @Override
    public void fake(int from, int to) {
        updateByTypeListOrInsert(from, to, Action.FAKE, Collections.singletonList(Action.FAKE));
    }

    @Override
    public void takeFake(int from, int to) {
        deleteAction(from, to, Action.FAKE.getValue());
    }

    private void updateByTypeListOrInsert(int from, int to, Action action, Collection<Action> typeList) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword())) {
            boolean putAlready;
            String typesLine = typeList.stream().map(Action::getValue).collect(Collectors.joining(","));

            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM matcha.LIKES_ACTION acts WHERE acts.FROM_USR = ? AND acts.TO_USR = ? AND FIND_IN_SET(acts.ACTION, ?) > 0",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                statement.setInt(1, from);
                statement.setInt(2, to);
                statement.setString(3, typesLine);
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
                    insert.setString(i, action.toString());

                    insert.execute();
                } catch (SQLException e) {
                    System.err.println("Не удалось добавить" + action.toString() + " от пользователя с id: " + from +
                            " пользователю: " + to);
                }
            }
            else {
                try (PreparedStatement update = connection.prepareStatement(
                        "UPDATE matcha.LIKES_ACTION acts SET CREATION_TIME = NOW(), acts.ACTION = ? WHERE acts.FROM_USR = ? AND acts.TO_USR = ? AND FIND_IN_SET(acts.ACTION, ?) > 0")) {
                    update.setString(i++, action.toString());
                    update.setInt(i++, from);
                    update.setInt(i++, to);
                    update.setString(i, typesLine);

                    update.execute();
                } catch (SQLException e) {
                    System.err.println("Не удалось обновить " + action.toString() + " от пользователя с id: " + from +
                            " пользователю: " + to);
                }
            }
        } catch (SQLException e) {
            System.err.println("Не удалось добавить лайк от пользователя с id: " + from +
                    " пользователю: " + to);
        }
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
    public void deleteAction(int from, int to, String action) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement putLikeAct = connection.prepareStatement(
                     "DELETE FROM matcha.LIKES_ACTION act WHERE FROM_USR = ? AND TO_USR = ? AND ACTION = ?"))
        {
            putLikeAct.setInt(1, from);
            putLikeAct.setInt(2, to);
            putLikeAct.setString(3, action);
            putLikeAct.execute();

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

    @Override
    public List<LikeAction> getNLikeForUserId(int id, int size) {
        return getNMatchUserIds(id, size, Action.LIKE.getValue());
    }

    @Override
    public List<LikeAction> getNFrom(Action action, int id, int size) {
        return getNMatchUserIds(id, size, action.getValue());
    }

    @Override
    public List<LikeAction> getNFromAfterId(Action action, int id, int specificId, int size) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement(
                     "WITH lastIdTime as (SELECT acts.ID, acts.CREATION_TIME FROM matcha.LIKES_ACTION acts WHERE acts.ID = ?)" +
                             "SELECT * FROM matcha.LIKES_ACTION acts, lastIdTime tm WHERE acts.FROM_USR = ? AND acts.ACTION = ? AND acts.CREATION_TIME < tm.CREATION_TIME " +
                             "ORDER BY acts.CREATION_TIME DESC LIMIT ?")) {
            int i = 1;
            statement.setInt(i++, specificId);
            statement.setInt(i++, id);
            statement.setString(i++, action.getValue());
            statement.setInt(i, size);
            statement.execute();

            return getIds(statement, action);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
    
    @Override
    public List<LikeAction> getNTo(Action action, int to, int size) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM matcha.LIKES_ACTION acts WHERE acts.TO_USR = ? AND acts.ACTION = ? ORDER BY acts.CREATION_TIME DESC LIMIT ?")) {
            statement.setInt(1, to);
            statement.setString(2, String.valueOf(action));
            statement.setInt(3, size);
            statement.execute();

            return getIds(statement, action);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public List<LikeAction> getNToAfterId(Action action, int to, int specificId, int size) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement(
                     "WITH lastIdTime as (SELECT acts.ID, acts.CREATION_TIME FROM matcha.LIKES_ACTION acts WHERE acts.ID = ?)" +
                             "SELECT * FROM matcha.LIKES_ACTION acts, lastIdTime tm WHERE acts.TO_USR = ? AND acts.ACTION = ? AND acts.CREATION_TIME < tm.CREATION_TIME " +
                             "ORDER BY acts.CREATION_TIME DESC LIMIT ?")) {
            int i = 1;
            statement.setInt(i++, specificId);
            statement.setInt(i++, to);
            statement.setString(i++, action.getValue());
            statement.setInt(i, size);
            statement.execute();

            return getIds(statement, action);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
    
    private List<LikeAction> getNMatchUserIds(int id, int size, String action) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM matcha.LIKES_ACTION acts WHERE acts.FROM_USR = ? AND ACTION = ? ORDER BY acts.CREATION_TIME DESC LIMIT ?")) {
            statement.setInt(1, id);
            statement.setString(2, action);
            statement.setInt(3, size);
            statement.execute();

            return getIds(statement, Action.valueOf(action));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private List<LikeAction> getIds(Statement statement, Action action) throws SQLException {
        try (ResultSet rs = statement.getResultSet()) {
            List<LikeAction> ids = new LinkedList<>();
            while (rs.next()) {
                LikeAction likeAction = new LikeAction();
                likeAction.setId(rs.getInt("ID"));
                likeAction.setCreationTime(new Date(rs.getTimestamp("CREATION_TIME").getTime()));
                likeAction.setFromUsr(rs.getInt("FROM_USR"));
                likeAction.setToUsr(rs.getInt("TO_USR"));
                likeAction.setAction(action);
                ids.add(likeAction);
            }
            return ids;
        }
    }

    public List<LikeAction> getByFromUsrOrToUsrAndAction(int id, String action) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM matcha.LIKES_ACTION acts WHERE acts.FROM_USR = ? OR acts.TO_USR = ? AND acts.ACTION = ?")) {
            int i = 1;
            statement.setInt(i++, id);
            statement.setInt(i++, id);
            statement.setString(i, action);
            statement.execute();

            try (ResultSet resultSet = statement.getResultSet()) {
                List<LikeAction> acts = new ArrayList<>(resultSet.getFetchSize());
                while (resultSet.next())
                    acts.add(fromResultSet(resultSet));
                return acts;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private LikeAction fromResultSet(ResultSet resultSet) throws SQLException {
        LikeAction likeAction = new LikeAction();
        likeAction.setFromUsr(resultSet.getInt("FROM_USR"));
        likeAction.setToUsr(resultSet.getInt("TO_USR"));
        likeAction.setAction(Action.valueOf(resultSet.getString("ACTION")));
        likeAction.setId(resultSet.getInt("ID"));
        likeAction.setCreationTime(new Date(resultSet.getTimestamp("CREATION_TIME").getTime()));
        return likeAction;
    }

    public List<Integer> getToUserDislikesByIds(int from, List<Integer> ids) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement(
                             "SELECT acts.TO_USR FROM matcha.LIKES_ACTION acts WHERE acts.FROM_USR = ? " +
                                     "AND FIND_IN_SET(acts.TO_USR, ?) > 0 AND acts.ACTION = 'DISLIKE' ORDER BY acts.CREATION_TIME")) {
            int i = 1;
            statement.setInt(i++, from);
            statement.setString(i, ids.stream().map(String::valueOf).collect(Collectors.joining(",")));
            statement.execute();

            try (ResultSet resultSet = statement.getResultSet()) {
                List<Integer> toUsrIds = new ArrayList<>();
                while (resultSet.next())
                    toUsrIds.add(resultSet.getInt("TO_USR"));
                return toUsrIds;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
