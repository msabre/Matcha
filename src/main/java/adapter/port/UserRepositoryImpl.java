package adapter.port;

import adapter.port.model.DBConfiguration;
import config.MyConfiguration;
import config.MyProperties;
import domain.entity.FilterParams;
import domain.entity.Photo;
import domain.entity.User;
import domain.entity.UserCard;
import usecase.port.FilterParamsRepository;
import usecase.port.UserCardRepository;
import usecase.port.UserRepository;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
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

                    uploadPhotosContetn(userCard);

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
    public void uploadPhotosContetn(UserCard card) {
        for (Photo photo : card.getPhotos()) {
            if (photo == null)
                continue;

            String path = String.format("%sIMG_%s_%s_%s.%s", MyProperties.IMAGES_PATH, card.getUserId(), "photo", photo.getNumber(), photo.getFormat());
            File file =  new File(path);

            if (file.exists()) {
                try {
                    byte[] content = Files.readAllBytes(Paths.get(path));
                    photo.setContent(new String(Base64.getEncoder().encode(content), StandardCharsets.UTF_8));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // TODO разобраться в потоках
        }
    }

    @Override
    public void setPhotosParams(List<Photo> photos) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement("UPDATE matcha.user_card SET PHOTOS_PARAMS = ? where ID = ?");)
        {
            String param = photos.stream()
                    .map(p -> p.getNumber() + "_" + p.getFormat())
                    .collect(Collectors.joining());

            statement.setString(1, param);
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    public List<User> getAllUserInSameLocation(String location, int id) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM matcha.user WHERE LOCATION = ? AND ID != ?"))
        {
            statement.setString(1, location);
            statement.setInt(2, id);
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

    @Override
    public void createChatBetweenTwoUsers(int usr1, int usr2) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement("INSERT INTO matcha.CHAT_AFFILIATION(?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, usr1);
            statement.setInt(2, usr2);
            statement.execute();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void getUsersChatList(int userId) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement("SELECT CHAT_ID FROM ",
                     Statement.RETURN_GENERATED_KEYS)) {

            statement.execute();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}




