package adapter.port;

import adapter.port.model.DBConfiguration;
import adapter.port.model.LocationTimeZoneUTC;
import config.MyConfiguration;
import domain.entity.FilterParams;
import domain.entity.User;
import domain.entity.UserCard;
import domain.entity.model.OnlineStatus;
import domain.entity.model.types.CityType;
import usecase.exception.EmailBusyException;
import usecase.exception.UserNameBusyException;
import usecase.port.FilterParamsRepository;
import usecase.port.UserCardRepository;
import usecase.port.UserRepository;

import java.sql.*;
import java.sql.Date;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

public class UserRepositoryImpl implements UserRepository {

    private static UserRepositoryImpl instance;

    private final DBConfiguration config = DBConfiguration.getConfig();;
    private final UserCardRepository userCardRepository = MyConfiguration.userCardRepository();
    private final FilterParamsRepository filterParamsRepository = MyConfiguration.filterParamsRepository();
    private final LocationTimeZoneUTC locationTimeZoneUTC = MyConfiguration.locationTimeZoneUTC();


    private UserRepositoryImpl() {

    }

    public static UserRepositoryImpl getRepository() {
        if (instance == null) {
            instance = new UserRepositoryImpl();
        }

        return instance;
    }

    @Override
    public int save(User user) throws UserNameBusyException, EmailBusyException {
        int userId = -1;

        if (!allFieldsUniqal(user))
            return userId;

        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword()))
        {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO matcha.user(CONFIRM, NAME, LASTNAME, MIDDLENAME, BIRTHDAY, YEARS_OLD, EMAIL, USERNAME, PASSWORD, LOCATION) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
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
            statement.setString(++i, user.getUserName());
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

    private boolean allFieldsUniqal(User user) throws UserNameBusyException, EmailBusyException {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
            PreparedStatement stat = connection.prepareStatement("SELECT * FROM matcha.user WHERE EMAIL = ? OR USERNAME = ?"))
        {
            stat.setString(1, user.getEmail());
            stat.setString(2, user.getUserName());
            stat.execute();

            ResultSet resultSet = stat.getResultSet();
            if (resultSet.next()) {
                if (resultSet.getString("EMAIL") != null)
                    throw new EmailBusyException();
                if (resultSet.getString("USERNAME") != null)
                    throw new UserNameBusyException();
            }
            return true;

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

            try (ResultSet resultSet = state.getResultSet()) {
                int id;
                if (resultSet.next()) {
                    id = resultSet.getInt(1);
                    return findById(id);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User findByUsername(String username) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement state = connection.prepareStatement("SELECT * FROM matcha.user where USERNAME= ?"))
        {
            state.setString(1, username);
            state.execute();

            try (ResultSet resultSet = state.getResultSet()) {
                int id;
                if (resultSet.next()) {
                    id = resultSet.getInt(1);

                    return findById(id);
                }
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
                    if (resultSet.next()) {
                        user.setId(resultSet.getInt("ID"));
                        user.setTokenConfirm(resultSet.getString("CONFIRM"));
                        user.setConfirm(user.getTokenConfirm() == null);
                        user.setFirstName(resultSet.getString("NAME"));
                        user.setLastName(resultSet.getString("LASTNAME"));
                        user.setMiddleName(resultSet.getString("MIDDLENAME"));
                        user.setBirthday(resultSet.getDate("BIRTHDAY"));
                        user.setYearsOld(resultSet.getInt("YEARS_OLD"));
                        user.setEmail(resultSet.getString("EMAIL"));
                        user.setPassword(resultSet.getString("PASSWORD"));
                        user.setLocation(resultSet.getString("LOCATION"));
                        user.setUserName(resultSet.getString("USERNAME"));
                        user.setLastAction(getTimeZoneFromResultSet(resultSet));

                        if (user.getLastAction() == null)
                            user.setStatus(OnlineStatus.Status.OFFLINE);
                        else {
                            ZonedDateTime currentTimeWithZone = ZonedDateTime.now(user.getLastAction().getZone());
                            long deltaMinutes = currentTimeWithZone.toLocalTime().getMinute() - user.getLastAction().toLocalTime().getMinute();
                            user.setStatus(deltaMinutes < 5 ? OnlineStatus.Status.ONLINE : OnlineStatus.Status.OFFLINE);
                        }

                        UserCard userCard = userCardRepository.findById(resultSet.getInt("USER_CARD"));
                        user.setCard(userCard);

                        FilterParams filter = filterParamsRepository.findById(resultSet.getInt("FILTER_PARAMS"));
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
                        "AND usr.ID NOT IN (SELECT acts.TO_USR FROM matcha.LIKES_ACTION acts WHERE acts.FROM_USR = ?) LIMIT ?";

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
    public void updateStatus(int id, ZoneId zoneId, OnlineStatus.Status status) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement("UPDATE matcha.user SET STATUS = ?, LAST_ACTION = ? where ID = ?"))
        {
            Timestamp current = Timestamp.valueOf(ZonedDateTime.now(zoneId).toLocalDateTime());
            statement.setString(1, status.toString());
            statement.setTimestamp(2, current);
            statement.setInt(3, id);

            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
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
    public void updateUsername(int id, String username) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement("UPDATE matcha.user SET USERNAME = ? where ID = ?"))
        {
            statement.setString(1, username);
            statement.setInt(2, id);

            statement.execute();
            System.out.println("username has been changed");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("username change error");
        }
    }


    @Override
    public void birthDateUpdate(int id, java.util.Date birthDate, int yearsOld) {
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

    // Метод для генератора
    @Override
    public List<Integer> getNUserIdsWithFreeChatByIds(String ids, int limit) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement("SELECT usr.ID FROM matcha.USER usr WHERE FIND_IN_SET(usr.ID, ?) > 0 " +
                     "AND usr.ID NOT IN " +
                        "(SELECT chat.FROM_USR FROM matcha.CHAT_AFFILIATION chat " +
                            "UNION " +
                        "SELECT chat.TO_USR FROM matcha.CHAT_AFFILIATION chat) LIMIT ?"))
        {
            statement.setString(1, ids);
            statement.setInt(2, limit);
            statement.execute();

            try (ResultSet resultSet = statement.getResultSet()) {
                List<Integer> result = new ArrayList<>();
                while (resultSet.next())
                    result.add(resultSet.getInt("ID"));
                return result;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public Map<Integer, String> getUserNamesByIds(List<Integer> userIds) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement("SELECT usr.ID, usr.NAME FROM matcha.user usr WHERE FIND_IN_SET(usr.ID, ?) > 0 "))
        {
            String idsLine = userIds.stream().map(String::valueOf).collect(Collectors.joining(","));
            statement.setString(1, idsLine);
            statement.execute();
            
            try (ResultSet resultSet = statement.getResultSet()) {
                Map<Integer, String> result = new HashMap<>();
                while (resultSet.next())
                    result.put(resultSet.getInt("ID"), resultSet.getString("NAME"));
                return result;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }

    @Override
    public List<OnlineStatus> getOnlineStatusByIds(Integer[] ids) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement("SELECT usr.ID, usr.STATUS, usr.LAST_ACTION, usr.LOCATION FROM matcha.user usr WHERE FIND_IN_SET(usr.ID, ?) > 0 "))
        {
            String idsLine = Arrays.stream(ids).map(String::valueOf).collect(Collectors.joining(","));
            statement.setString(1, idsLine);
            statement.execute();

            try (ResultSet resultSet = statement.getResultSet()) {
                List<OnlineStatus> result = new ArrayList<>(resultSet.getFetchSize());
                while (resultSet.next()) {
                    OnlineStatus onlineStatus = new OnlineStatus();
                    onlineStatus.setLastAction(getTimeZoneFromResultSet(resultSet));
                    onlineStatus.setStatus(OnlineStatus.Status.fromString(resultSet.getString("STATUS")));
                    onlineStatus.setUserId(resultSet.getInt("ID"));
                    result.add(onlineStatus);
                }
                return result;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private ZonedDateTime getTimeZoneFromResultSet(ResultSet resultSet) throws SQLException {
        ZonedDateTime timeWithZone = null;
        try {
            ZoneId zoneId = locationTimeZoneUTC.getZoneIdByCity(resultSet.getString("LOCATION"));
            LocalDateTime localDateTime = resultSet.getTimestamp("LAST_ACTION").toLocalDateTime();
            timeWithZone = localDateTime.atZone(zoneId);

        } catch (Exception e) {
            System.err.println("Ошибка обработки временной зоны пользователя: " + resultSet.getInt("ID"));
        }

        return timeWithZone;
    }
}




