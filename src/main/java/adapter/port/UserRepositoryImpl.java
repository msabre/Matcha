package adapter.port;

import adapter.port.model.DBConfiguration;
import config.MyConfiguration;
import config.MyProperties;
import domain.entity.FilterParams;
import domain.entity.Photo;
import domain.entity.User;
import domain.entity.UserCard;
import domain.entity.model.UserMatch;
import usecase.port.FilterParamsRepository;
import usecase.port.UserCardRepository;
import usecase.port.UserRepository;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

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
            PreparedStatement statement = connection.prepareStatement("INSERT INTO matcha.user(CONFIRM, NAME, LASTNAME, MIDDLENAME, BIRTHDAY, YEARS_OLD, EMAIL, PASSWORD, LOCATION) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            int i = 0;

            statement.setString(++i, user.getTokenConfirm());
            statement.setString(++i, user.getFirstName());
            statement.setString(++i, user.getLastName());
            statement.setString(++i, user.getMiddleName());

            Date date = new Date(Optional.ofNullable(user.getBirthday()).map(java.util.Date::getTime).orElse(10000000L));
            statement.setDate(++i, date);
            statement.setInt(++i, user.getYearsOld());
            statement.setString(++i, user.getEmail());
            statement.setString(++i, user.getPassword());
            statement.setString(++i, user.getLocation());

            statement.execute();

            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next())
                userId = rs.getInt(1);

            if (userId < 1)
                return userId;

            int cardId = -1;
            try (PreparedStatement newUserCardLine = connection.prepareStatement("INSERT INTO matcha.user_card(BIOGRAPHY, WORKPLACE, POSITION, EDUCATION, GENDER,SEXUAL_PREFERENCE, TAGS, RATING, USER_ID) " +
                    "VALUES(NULL, NULL, NULL, NULL, ?, ?, NULL, NULL, ?)", Statement.RETURN_GENERATED_KEYS)) {
                newUserCardLine.setString(1, user.getCard().getGender().getValue());
                newUserCardLine.setString(2, user.getCard().getSexualPreference().getValue());
                newUserCardLine.setInt(3, userId);

                newUserCardLine.execute();

                ResultSet rsId = newUserCardLine.getGeneratedKeys();

                if (rsId.next()) {
                    cardId = rsId.getInt(1);
                    user.getCard().setId(cardId);
                    user.getCard().setUserId(user.getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (cardId > 0) {
                try (PreparedStatement addCardId = connection.prepareStatement("UPDATE matcha.user SET USER_CARD=? WHERE ID = ?")) {
                    addCardId.setInt(1, cardId);
                    addCardId.setInt(2, userId);

                    addCardId.execute();
                }
            }

            int filterId = -1;
            try (PreparedStatement newFilter = connection.prepareStatement(
                    "INSERT INTO matcha.FILTER_PARAMS(AGE_BY, AGE_TO, RATING, COMMON_TAGS_COUNT, LOCATION) VALUES(18, 45, 0.0, 0, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {

                newFilter.setString(1, user.getLocation());
                newFilter.execute();

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
                int id;
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
        public User findById(int id) {
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
                        int i = 0;

                        user.setId(resultSet.getInt(++i));
                        user.setTokenConfirm(resultSet.getString(++i));
                        user.setConfirm(user.getTokenConfirm() == null);
                        user.setFirstName(resultSet.getString(++i));
                        user.setLastName(resultSet.getString(++i));
                        user.setMiddleName(resultSet.getString(++i));
                        user.setBirthday(resultSet.getDate(++i));
                        user.setYearsOld(resultSet.getInt(++i));
                        user.setEmail(resultSet.getString(++i));
                        user.setPassword(resultSet.getString(++i));
                        user.setLocation(resultSet.getString(++i));

                        UserCard userCard = userCardRepository.findById(resultSet.getInt(++i));
                        user.setCard(userCard);

                        FilterParams filter = filterParamsRepository.findById(resultSet.getInt(++i));
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
    public void confirmById(int id) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement("UPDATE matcha.user SET CONFIRM = NULL where ID = ?"))
        {
            statement.setInt(1, id);
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean passwordUpdate(int id, String password) {
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


//    "AND acts.ACTION IN ('LIKE', 'MATCH')

    @Override
    public LinkedList<User> getNewForActionUsersWithParams(List<Integer> currentIds, String location, int id, int age_by, int age_to, List<String> preferencesParams, int limit) {
        String query =
                "SELECT DISTINCT usr.ID FROM matcha.user usr " +
                        "INNER JOIN matcha.user_card card ON card.ID = usr.USER_CARD " +
                        "WHERE FIND_IN_SET(usr.ID, ?) <= 0 " +
                        "AND usr.LOCATION = ? " +
                        "AND usr.ID != ? " +
                        "AND usr.YEARS_OLD >= ? " +
                        "AND usr.YEARS_OLD <= ? " +
                        "AND FIND_IN_SET(card.GENDER, ?) > 0 " +
                        "AND FIND_IN_SET(card.SEXUAL_PREFERENCE, ?) > 0 " +
                        "AND usr.ID NOT IN (SELECT acts.ID FROM matcha.LIKES_ACTION acts WHERE acts.FROM_USR = ?) LIMIT ?";

        return getList(currentIds, query, location, id, age_by, age_to, preferencesParams, limit);
    }

    @Override
    public LinkedList<User> getDislikeUsersWithParams(List<Integer> currentIds, String location, int id, int age_by, int age_to, List<String> preferencesParams, int limit) {
        String query =
                "SELECT DISTINCT usr.ID FROM matcha.user usr " +
                "INNER JOIN matcha.user_card card ON card.ID = usr.USER_CARD " +
                "WHERE FIND_IN_SET(usr.ID, ?) <= 0 " +
                "AND usr.LOCATION = ? " +
                "AND usr.ID != ? " +
                "AND usr.YEARS_OLD >= ? " +
                "AND usr.YEARS_OLD <= ? " +
                "AND FIND_IN_SET(card.GENDER, ?) > 0 " +
                "AND FIND_IN_SET(card.SEXUAL_PREFERENCE, ?) > 0 " +
                "AND usr.ID IN (SELECT acts.TO_USR FROM matcha.LIKES_ACTION acts WHERE acts.FROM_USR = ? AND acts.ACTION IN ('DISLIKE')) LIMIT ?";

        return getList(currentIds, query, location, id, age_by, age_to, preferencesParams, limit);
    }

    private LinkedList<User> getList(List<Integer> currentIds, String query, String location, int id, int age_by, int age_to, List<String> preferencesParams, int limit) {
        LinkedList<User> usersList = new LinkedList<>();
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
            PreparedStatement statement = connection.prepareStatement(query))
        {
            String currentIdsLine = currentIds.stream().map(String::valueOf).collect(Collectors.joining(","));
            String gender = preferencesParams.stream().map(s -> s.split(";")[0]).distinct().collect(Collectors.joining(","));
            String sexualPreferences = preferencesParams.stream().map(s -> s.split(";")[1]).distinct().collect(Collectors.joining(","));

            int i = 1;
            statement.setString(i++, currentIdsLine);
            statement.setString(i++, location);
            statement.setInt(i++, id);
            statement.setInt(i++, age_by);
            statement.setInt(i++, age_to);
            statement.setString(i++, gender);
            statement.setString(i++, sexualPreferences);
            statement.setInt(i++, id);
            statement.setInt(i, limit);
            statement.execute();

            ResultSet rs = null;
            try {
                rs = statement.getResultSet();
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
            return usersList;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usersList;
    }

    @Override
    public void updateEmail(int id, String email) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement("UPDATE matcha.user SET EMAIL = ? where ID = ?"))
        {

            statement.setString(1, email);
            statement.setInt(2, id);

            statement.execute();
            System.out.println("email has been changed");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("email change error");
        }
    }

    @Override
    public void updateFio(int id, String[] fio) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement("UPDATE matcha.user SET LASTNAME = ?, NAME = ?, MIDDLENAME = ? where ID = ?"))
        {
            statement.setString(1, fio[0]);
            statement.setString(2, fio[1]);
            statement.setString(3, fio[2]);
            statement.setInt(4, id);

            statement.execute();
            System.out.println("fio has been changed");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("fio change error");
        }
    }

    @Override
    public void birhDateUpdate(int id, java.util.Date birthDate, int yearsOld) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement("UPDATE matcha.user SET BIRTHDAY = ?, YEARS_OLD = ? where ID = ?"))
        {
            statement.setDate(1, new Date(birthDate.getTime()));
            statement.setInt(2, yearsOld);
            statement.setInt(3, id);

            statement.execute();
            System.out.println("birthDate has been changed");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("birthDate change error");
        }
    }
}




