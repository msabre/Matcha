package adapter.port;

import adapter.port.model.DBConfiguration;
import domain.entity.FilterParams;
import domain.entity.Link;
import javafx.util.Pair;
import usecase.port.FilterParamsRepository;

import java.sql.*;

public class FilterParamsRepositoryImpl implements FilterParamsRepository {
    private static FilterParamsRepositoryImpl instance;


    private final DBConfiguration config = DBConfiguration.getConfig();;

    private FilterParamsRepositoryImpl() {
    }

    public static FilterParamsRepositoryImpl getRepository() {
        if (instance == null) {
            instance = new FilterParamsRepositoryImpl();
        }

        return instance;
    }

    @Override
    public void update(FilterParams filter) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement("UPDATE matcha.FILTER_PARAMS SET AGE_BY = ?, AGE_TO = ?, RATING = ?," +
                     "COMMON_TAGS_COUNT = ?, LOCATION = ? WHERE ID = ?")) {

            if (filter.getAgeBy() == null)
                filter.setAgeBy(18);
            if (filter.getAgeTo() == null)
                filter.setAgeTo(45);
            if (filter.getRating() == null)
                filter.setRating(0.0);
            if (filter.getCommonTagsCount() == null)
                filter.setCommonTagsCount(0);

            int i = 1;
            statement.setInt(i++, filter.getAgeBy());
            statement.setInt(i++, filter.getAgeTo());
            statement.setDouble(i++, filter.getRating());
            statement.setInt(i++, filter.getCommonTagsCount());
            statement.setString(i++, filter.getLocation());
            statement.setInt(i, filter.getId());

            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FilterParams findById(Integer id) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement state = connection.prepareStatement("SELECT * FROM matcha.FILTER_PARAMS where ID = ?"))
        {
            state.setInt(1, id);
            state.execute();

            ResultSet resultSet = null;
            try {
                resultSet = state.getResultSet();
                while (resultSet.next()) {
                    FilterParams filter = new FilterParams();
                    filter.setId(id);

                    filter.setAgeBy(resultSet.getInt(2));
                    filter.setAgeTo(resultSet.getInt(3));
                    filter.setRating(resultSet.getDouble(4));
                    filter.setCommonTagsCount(resultSet.getInt(5));
                    filter.setLocation(resultSet.getString(6));

                    return filter;
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            finally {
                if (resultSet != null)
                    resultSet.close();
                else
                    System.err.println("Ошибка получения фильтра пользователя с БД!");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
