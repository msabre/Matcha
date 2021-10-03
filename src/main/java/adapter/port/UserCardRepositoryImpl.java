package adapter.port;

import adapter.port.model.DBConfiguration;
import domain.entity.Photo;
import domain.entity.UserCard;
import domain.entity.model.types.Action;
import domain.entity.model.types.GenderType;
import domain.entity.model.types.SexualPreferenceType;
import usecase.port.UserCardRepository;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

                    String params = resultSet.getString(++i);
                    card.setPhotos(new ArrayList<>(Collections.nCopies(5, null)));
                    if (params!= null && !params.isEmpty()) {
                        for (String photoParam : params.split(";")) {
                            String[] detail = photoParam.split("_");

                            Photo photo = new Photo();
                            photo.setNumber(detail[0]);
                            photo.setFormat(detail[1]);
                            card.getPhotos().set(Integer.parseInt(detail[0]) - 1, photo);
                        }
                    }
                    card.setUserId(resultSet.getInt(++i));
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

    @Override
    public void updateUserActions(UserCard userCard) {
        userCard.setActionMap(getUserLikesAction(userCard.getUserId()));
        userCard.setLikes(getActsListFromMap(userCard.getActionMap(), Action.LIKE));
        userCard.setDisLikes(getActsListFromMap(userCard.getActionMap(), Action.DISLIKE));
        userCard.setMatches(getActsListFromMap(userCard.getActionMap(), Action.MATCH));
    }

    private List<Integer> getActsListFromMap(Map<Integer, Action> actionMap, Action action ) {
        return actionMap.entrySet().stream()
                .filter(entry -> action.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private LinkedHashMap<Integer, Action> getUserLikesAction(Integer userId) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM matcha.LIKES_ACTION WHERE FROM_USR = ? ORDER BY CREATION_TIME"))
        {
            statement.setInt(1, userId);
            statement.execute();

            ResultSet rs = null;
            try {
                rs = statement.getResultSet();
                LinkedHashMap<Integer, Action> likesList = new LinkedHashMap<>();
                while (rs.next()) {
                    likesList.put(rs.getInt("TO_USR"), Action.valueOf(rs.getString("ACTION")));
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
    public void increaseRating(int id, double increase) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement("SELECT RATING FROM matcha.user_card WHERE ID =?"))
        {
            statement.setInt(1, id);
            statement.execute();

            double rating = 0;
            ResultSet rs = statement.getResultSet();
            while (rs.next()) {
                rating = rs.getDouble("RATING");
                rating += increase;
                break;
            }

            try (PreparedStatement newRating = connection.prepareStatement("UPDATE matcha.user_card SET RATING = ? WHERE id = ?")) {
                newRating.setDouble(1, rating);
                newRating.setInt(2, id);
                newRating.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void decreaseRating(int id, double decrease) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement("SELECT RATING FROM matcha.user_card WHERE ID =?"))
        {
            statement.setInt(1, id);
            statement.execute();

            double rating = 0;
            ResultSet rs = statement.getResultSet();
            while (rs.next()) {
                rating = rs.getDouble("RATING");
                rating -= decrease;
                if (rating < 0)
                    rating = 0;
                break;
            }

            try (PreparedStatement newRating = connection.prepareStatement("UPDATE matcha.user_card SET RATING = ? WHERE id = ?")) {
                newRating.setDouble(1, rating);
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

            List<String> listResult = new ArrayList<>(Collections.nCopies(5, null));
            if (oldParams != null && !oldParams.isEmpty()) {
                for (String photoParam : oldParams.split(";")) {
                    String[] detail = photoParam.split("_");
                    listResult.set(Integer.parseInt(detail[0]) - 1, photoParam);
                }
            }
            photoList.forEach(photo -> {
                int index = Integer.parseInt(photo.getNumber()) - 1;
                switch (photo.getAction()) {
                    case "save":
                        listResult.set(index, String.format("%s_%s", photo.getNumber(), photo.getFormat()));
                        break;
                    case "delete":
                        listResult.set(index, null);
                    default:
                        break;
                }
            });

            String result = listResult.stream()
                    .filter(Objects::nonNull)
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

    @Override
    public List<Photo> getIconsByIds(Collection<Integer> ids) {
        try(Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
            PreparedStatement statement = connection.prepareStatement("SELECT usr.PHOTOS_PARAMS, usr.MAIN_PHOTO FROM matcha.user_card usr WHERE FIND_IN_SET(usr.ID, ?) > 0")) {

            String idsLine = ids.stream().map(Object::toString).collect(Collectors.joining(","));
            statement.setString(1, idsLine);
            statement.execute();

            ResultSet resultSet = statement.getResultSet();
            List<Photo> photoList = new LinkedList<>();
            while (resultSet.next()) {
                Photo photo = new Photo();
                photo.setNumber(String.valueOf(resultSet.getInt("MAIN_PHOTO")));

                String[] photosParams = Optional.ofNullable(resultSet.getString("PHOTOS_PARAMS")).orElse("").split(";");
                String format = null;
                for (String param : photosParams) {
                    String[] array = param.split("_");
                    if (array[0].equals(photo.getNumber())) {
                        format = array[1];
                        break;
                    }
                }
                photo.setFormat(format);
                photo.setMain(true);
                photoList.add(photo);
            }
            return photoList;

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Collections.emptyList();
    }
}
