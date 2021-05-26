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
                     "COMMON_TAGS_COUNT = ? WHERE ID = ?")) {

            if (filter.getAgeBy() == null || filter.getAgeTo() == null) {
                filter.setAgeBy(18);
                filter.setAgeTo(45);
            }
            statement.setInt(1, filter.getAgeBy());
            statement.setInt(2, filter.getAgeTo());
            statement.setDouble(3, filter.getRating());
            statement.setInt(4, filter.getCommonTagsCount());

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
