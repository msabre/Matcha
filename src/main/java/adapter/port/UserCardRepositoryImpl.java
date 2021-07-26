package adapter.port;

import adapter.port.model.DBConfiguration;
import domain.entity.Link;
import domain.entity.Photo;
import domain.entity.UserCard;
import domain.entity.model.types.GenderType;
import domain.entity.model.types.SexualPreferenceType;
import usecase.port.UserCardRepository;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class UserCardRepositoryImpl implements UserCardRepository {
    private static UserCardRepositoryImpl instance;


    private final DBConfiguration config = DBConfiguration.getConfig();;

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
             PreparedStatement statement = connection.prepareStatement("UPDATE matcha.user_card SET BIOGRAPHY = ?, WORKPLACE = ?, POSITION = ?, EDUCATION = ?, GENDER = ?, SEXUAL_PREFERENCE = ?, TAGS = ?, RATING = ?, PHOTOS_PARAMS = null WHERE ID = ?", Statement.RETURN_GENERATED_KEYS)) {

            int i = 0;

            statement.setString(++i, card.getBiography());
            statement.setString(++i, card.getWorkPlace());
            statement.setString(++i, card.getPosition());
            statement.setString(++i, card.getEducation());
            statement.setString(++i, card.getGender().getValue());
            statement.setString(++i, card.getSexualPreference().getValue());

            StringBuilder tags = new StringBuilder();
            for (String tag : card.getTags())
                tags.append(tag).append(";");

            statement.setString(++i, tags.toString());
            statement.setDouble(++i, card.getRating());
            statement.setInt(++i, card.getId());
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
                    int i = 0;
                    UserCard card = new UserCard();
                    card.setId(resultSet.getInt(++i));
                    card.setBiography(resultSet.getString(++i));
                    card.setWorkPlace(resultSet.getString(++i));
                    card.setPosition(resultSet.getString(++i));
                    card.setEducation(resultSet.getString(++i));
                    card.setGender(GenderType.fromStr(resultSet.getString(++i)));
                    card.setSexualPreference(SexualPreferenceType.fromStr(resultSet.getString(++i)));

                    String[] arrayTags = Optional.ofNullable(resultSet.getString(++i)).
                            map(regex -> regex.split(";")).orElse(new String[0]);

                    List<String> tags = Arrays.asList(arrayTags);
                    card.setTags(tags);
                    card.setRating(resultSet.getDouble(++i));
                    card.setPhotos(new ArrayList<>(5));

                    String params = resultSet.getString(++i);
                    card.setPhotos(new ArrayList<>(5));
                    if (params!= null && !params.isEmpty()) {
                        for (String photoParam : params.split(";")) {
                            String[] detail = photoParam.split("_");

                            Photo photo = new Photo();
                            photo.setFormat(detail[0]);
                            photo.setNumber(detail[1]);
                            card.getPhotos().add(photo);
                        }
                    }
                    Integer user_id = resultSet.getInt(++i);
                    card.setLikes(getUserLikesAction(user_id, "like"));
                    card.setDislikes(getUserLikesAction(user_id, "dislike"));

                    return card;
                }
            }
            catch (Exception e) {
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
             PreparedStatement statement = connection.prepareStatement("SELECT RAITING FROM matcha.user_card WHERE ID =?"))
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

    @Override
    public void updatePhotosParams(int cardId, List<Photo> photoList) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement("select PHOTOS_PARAMS from matcha.user_card WHERE ID =?"))
        {
            statement.setInt(1, cardId);
            statement.execute();

            ResultSet rs = statement.getResultSet();
            String oldParams = null;
            if (rs.next())
                oldParams = rs.getString(1);

            List<Photo> current = new ArrayList<>(5);
            if (oldParams!= null && !oldParams.isEmpty()) {
                for (String photoParam : oldParams.split(";")) {
                    String[] detail = photoParam.split("_");

                    Photo photo;
                    int index = Integer.parseInt(detail[1]);
                    if (photoList.get(index) == null) {
                        photo = new Photo();
                        photo.setFormat(detail[0]);
                        photo.setNumber(detail[1]);
                    } else {
                        photo = photoList.get(index);
                        if (photo.getAction().equals("delete"))
                            photo = null;
                    }
                    current.add(index, photo);
                }
            }

            String result = current.stream()
                    .filter(Objects::nonNull)
                    .map(p -> String.format("%s_%s", p.getFormat(), p.getFormat()))
                    .collect(Collectors.joining(";"));

            try (PreparedStatement updateLine = connection.prepareStatement("update matcha.user_card set PHOTOS_PARAMS = ? WHERE ID =?")) {
                updateLine.setString(1, result);
                updateLine.setInt(2, cardId);
                updateLine.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
