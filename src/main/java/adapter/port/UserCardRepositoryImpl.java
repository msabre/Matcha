package adapter.port;

import adapter.port.model.DBConfiguration;
import domain.entity.Link;
import domain.entity.UserCard;import usecase.port.UserCardRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UserCardRepositoryImpl implements UserCardRepository {
    private static UserCardRepositoryImpl instance;


    private DBConfiguration config = DBConfiguration.getConfig();;

    private UserCardRepositoryImpl() {
    }

    public static UserCardRepositoryImpl getRepository() {
        if (instance == null) {
            instance = new UserCardRepositoryImpl();
        }

        return instance;
    }

    @Override
    public UserCard save(UserCard card) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement("UPDATE matcha.user_card SET GENDER = ?, SEXUAL_PREFERENCE = ?, " +
                     "BIOGRAPHY = ?, WORKPLACE = ?, POSITION = ?, EDUCATION = ?, TAGS = ?, RATING = ?, YEARS_OLD = ? WHERE ID = ?", Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, card.getGender());
            statement.setString(2, card.getSexual_preference());
            statement.setString(3, card.getBiography());
            statement.setString(4, card.getWorkPlace());
            statement.setString(5, card.getPosition());
            statement.setString(6, card.getEducation());

            StringBuilder tags = new StringBuilder();
            for (String tag : card.getTags())
                tags.append(tag).append(";");

            statement.setString(7, tags.toString());
            statement.setDouble(8, card.getRating());
            statement.setInt(9, card.getYearsOld());
            statement.setInt(10, card.getId());
            statement.execute();

            return findById(card.getId());

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public UserCard findById(Integer id) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement state = connection.prepareStatement("SELECT * FROM matcha.user_card where ID = ?"))
        {
            state.setInt(1, id);
            state.execute();

            ResultSet resultSet = null;
            try {
                resultSet = state.getResultSet();
                while (resultSet.next()) {
                    UserCard card = new UserCard();
                    card.setId(resultSet.getInt(1));
                    card.setGender(resultSet.getString(2));
                    card.setSexual_preference(resultSet.getString(3));
                    card.setBiography(resultSet.getString(4));
                    card.setWorkPlace(resultSet.getString(5));
                    card.setPosition(resultSet.getString(6));
                    card.setEducation(resultSet.getString(7));

                    String[] arrayTags = Optional.ofNullable(resultSet.getString(8)).
                            map(regex -> regex.split(";")).orElse(new String[0]);

                    List<String> tags = Arrays.asList(arrayTags);
                    card.setTags(tags);
                    card.setRating(resultSet.getDouble(9));
                    card.setYearsOld(resultSet.getInt(10));

                    Integer user_id = resultSet.getInt(11);
                    card.setLikes(getUserLikesAction(user_id, "like"));
                    card.setDislikes(getUserLikesAction(user_id, "dislike"));

                    return card;
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            finally {
                if (resultSet != null)
                    resultSet.close();
                else
                    System.err.println("Ошибка получения карточки пользователя с БД!");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Integer> getUserLikesAction(Integer userId, String action) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM matcha.LIKES_ACTION WHERE FROM_USR = ? AND ACTION = ?"))
        {
            statement.setInt(1, userId);
            statement.setString(2, action);
            statement.execute();

            ResultSet rs = null;
            try {
                rs = statement.getResultSet();
                List<Integer> likesList = new ArrayList<>();
                while (rs.next()) {
                    Integer id = rs.getInt(2);
                    likesList.add(id);
                }
                return likesList;

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
    public void increaseRating(int id, double increse) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement("SELECT RAITING FROM matcha.user_card WHERE  RATING =?"))
        {
            statement.setInt(1, id);
            statement.execute();

            double currentRating = 0;
            ResultSet rs = statement.getResultSet();
            while (rs.next()) {
                currentRating += rs.getDouble("RATING");
                break;
            }

            try (PreparedStatement newRating = connection.prepareStatement("UPDATE matcha.user_card SET RATING = ? WHERE id = ?")) {
                newRating.setDouble(1, currentRating + increse);
                newRating.setInt(2, id);
                newRating.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
