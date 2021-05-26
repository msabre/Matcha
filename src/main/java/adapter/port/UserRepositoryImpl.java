package adapter.port;

import adapter.port.model.DBConfiguration;
import config.MyConfiguration;
import domain.entity.FilterParams;
import domain.entity.Link;
import domain.entity.User;
import domain.entity.UserCard;import usecase.JDBC_lessons.third_lesson.PrepareStatement;
import usecase.port.FilterParamsRepository;
import usecase.port.UserCardRepository;
import usecase.port.UserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {

    private static UserRepositoryImpl instance;

    private final DBConfiguration config = DBConfiguration.getConfig();;
    private final UserCardRepository userCardRepository = MyConfiguration.userCardRepository();
    private final FilterParamsRepository filterParamsRepository = MyConfiguration.filterParamsRepository();


    private UserRepositoryImpl() {

    }

    public static UserRepositoryImpl getRepository() {
        if (instance == null) {
            instance = new UserRepositoryImpl();
        }

        return instance;
    }

    @Override
    public int save(User user) {
        int userId = -1;

        if (!allFieldsUniqal(user))
            return userId;

        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword()))
        {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO matcha.user(CONFIRM, NAME, LASTNAME, MIDDLENAME, EMAIL, PASSWORD, LOCATION) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getTokenConfirm());
            statement.setString(2, user.getFirstName());
            statement.setString(3, user.getLastName());
            statement.setString(4, user.getMiddleName());
            statement.setString(5, user.getEmail());
            statement.setString(6, user.getPassword());
            statement.setString(7, user.getLocation());

            statement.execute();

            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next())
                userId = rs.getInt(1);

            if (userId < 1)
                return userId;

            int cardId = -1;
            try (Statement newUserCardLine = connection.createStatement()) {
                newUserCardLine.execute("INSERT INTO matcha.user_card(GENDER, SEXUAL_PREFERENCE, BIOGRAPHY, WORKPLACE, POSITION, EDUCATION, TAGS, RATING, YEARS_OLD, USER_ID) " +
                        "VALUES(NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, " + userId + ")", Statement.RETURN_GENERATED_KEYS);

                ResultSet rsId = newUserCardLine.getGeneratedKeys();

                if (rsId.next()) {
                    user.setCard(new UserCard());

                    cardId = rsId.getInt(1);
                    user.getCard().setId(cardId);
                    user.getCard().setUserId(user.getId());
                }
            }

            if (cardId > 0) {
                try (PreparedStatement addCardId = connection.prepareStatement("UPDATE matcha.user SET USER_CARD=? WHERE ID = ?")) {
                    addCardId.setInt(1, cardId);
                    addCardId.setInt(2, userId);

                    addCardId.execute();
                }
            }

            int filterId = -1;
            try (Statement newFilter = connection.createStatement()) {
                newFilter.execute("INSERT INTO matcha.FILTER_PARAMS(AGE_BY, AGE_TO, RATING, COMMON_TAGS_COUNT) " +
                        "VALUES(18, 45, NULL, NULL)", Statement.RETURN_GENERATED_KEYS);

                ResultSet rsId = newFilter.getGeneratedKeys();
                if (rsId.next()) {
                    user.setFilter(new FilterParams());

                    filterId = rsId.getInt(1);
                    user.getFilter().setId(filterId);
                }
            }

            if (filterId > 0) {
                try (PreparedStatement addCardId = connection.prepareStatement("UPDATE matcha.user SET FILTER_PARAMS =? WHERE ID = ?")) {
                    addCardId.setInt(1, filterId);
                    addCardId.setInt(2, userId);

                    addCardId.execute();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userId;
    }

    private boolean allFieldsUniqal(User user) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
            PreparedStatement stat = connection.prepareStatement("SELECT * FROM matcha.user WHERE EMAIL = ?"))
        {
            stat.setString(1, user.getEmail());
            stat.execute();

            ResultSet resultSet = stat.getResultSet();
            return !resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public User findByEmail(String email) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement state = connection.prepareStatement("SELECT * FROM matcha.user where EMAIL= ?"))
        {
            state.setString(1, email);
            state.execute();
            
            ResultSet resultSet = null;
            try {
                resultSet = state.getResultSet();
                Integer id;
                while (resultSet.next()) {
                    id = resultSet.getInt(1);

                    return findById(id);
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            finally {
                if (resultSet != null)
                    resultSet.close();
                else
                    System.err.println("Пользователь с данным email не найден!");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User findById(Integer id) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement state = connection.prepareStatement("SELECT * FROM matcha.user where ID = ?"))
        {
            state.setInt(1, id);
            state.execute();

            ResultSet resultSet = null;
            try {
                resultSet = state.getResultSet();
                User user = new User();
                while (resultSet.next()) {
                    user.setId(resultSet.getInt(1));
                    user.setTokenConfirm(resultSet.getString(2));

                    if (user.getTokenConfirm() == null)
                        user.setConfirm(true);
                    else
                        user.setConfirm(false);

                    user.setFirstName(resultSet.getString(4));
                    user.setLastName(resultSet.getString(5));
                    user.setMiddleName(resultSet.getString(6));
                    user.setEmail(resultSet.getString(7));
                    user.setPassword(resultSet.getString(8));
                    user.setLocation(resultSet.getString(9));

                    UserCard userCard = userCardRepository.findById(resultSet.getInt(10));
                    user.setCard(userCard);

                    FilterParams filter = filterParamsRepository.findById(resultSet.getInt(11));
                    user.setFilter(filter);

                    return user;
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            finally {
                if (resultSet != null)
                    resultSet.close();
                else
                    System.err.println("Ошибка получения пользователя с БД!");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void confirmById(Integer id) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword()))
        {
            PreparedStatement statement = connection.prepareStatement("UPDATE matcha.user SET CONFIRM = NULL where ID = ?");
            statement.setInt(1, id);

            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean passwordUpdate(Integer id, String password) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement("UPDATE matcha.user SET PASSWORD = ? where ID = ?"))
        {

                statement.setString(1, password);
                statement.setInt(2, id);

                statement.execute();
                System.out.println("password has been changed");
                return true;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("password change error");
        }
        return false;
    }

    @Override
    public List<User> getAllUserInSameLocation(String location) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM matcha.user WHERE LOCATION = ?"))
        {
            statement.setString(1, location);
            statement.execute();

            ResultSet rs = null;
            try {
                rs = statement.getResultSet();
                List<User> usersList = new ArrayList<>();
                while (rs.next()) {
                    User user = findById(rs.getInt(1));
                    usersList.add(user);
                }
                return usersList;

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (rs != null)
                    rs.close();
                else
                    System.err.println("Пользователи по данному запросы не найдены!");
            }

            return null;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}




