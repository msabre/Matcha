package adapter.port;

import adapter.port.model.DBConfiguration;
import domain.entity.Link;
import usecase.port.UrlRepository;

import java.sql.*;

public class UrlRepositoryImpl implements UrlRepository {

    private static UrlRepositoryImpl instance;

    private final DBConfiguration config = DBConfiguration.getConfig();;

    private UrlRepositoryImpl() {

    }

    public static UrlRepository getRepository() {
        if (instance == null) {
            instance = new UrlRepositoryImpl();
        }

        return instance;
    }

    @Override
    public Integer addLink(String url) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword()))
        {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO matcha.link(URL, OPEN) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, url);
            statement.setInt(2, 0);

            statement.execute();

            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next())
                return rs.getInt(1);
            return -1;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public Link getLink(Integer id) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement state = connection.prepareStatement("SELECT * FROM matcha.link where ID= ?"))
        {
            state.setInt(1, id);
            state.execute();

            ResultSet resultSet = null;
            try {
                resultSet = state.getResultSet();
                Link link = new Link();
                while (resultSet.next()) {
                    link.setId(resultSet.getInt(1));
                    link.setToken(resultSet.getString(2));
                    link.setOpen(resultSet.getInt(3) == 1);

                    return link;
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
    public void markLink(Integer id) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement state = connection.prepareStatement("UPDATE matcha.link SET OPEN = 1 WHERE ID= ?"))
        {
            state.setInt(1, id);
            state.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
