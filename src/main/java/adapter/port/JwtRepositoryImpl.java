package adapter.port;

import adapter.port.model.DBConfiguration;
import domain.entity.JsonWebToken;
import usecase.port.JwtRepository;

import java.sql.*;


public class JwtRepositoryImpl implements JwtRepository {
    private static JwtRepository instance;

    private final DBConfiguration config = DBConfiguration.getConfig();


    private JwtRepositoryImpl() {

    }

    public static JwtRepository getRepository() {
        if (instance == null) {
            instance = new JwtRepositoryImpl();
        }

        return instance;
    }

    @Override
    public boolean putToken(JsonWebToken token) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
            PreparedStatement putToken = connection.prepareStatement("INSERT  INTO matcha.JWT(USER_ID, TOKEN) VALUES(?, ?)")) {

            putToken.setString(2, token.getToken());
            putToken.setInt(1, token.getUserId());
            putToken.execute();

            return true;

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Integer getTokenId(String token) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement checkLine = connection.prepareStatement("SELECT * FROM matcha.JWT where TOKEN = ?"))
        {

            checkLine.setString(1, token);
            checkLine.execute();

            try (ResultSet rs = checkLine.getResultSet()) {
                while (rs.next())
                    return rs.getInt("ID");

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void dropTokenById(Integer id) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement putToken = connection.prepareStatement("DELETE FROM matcha.JWT WHERE ID = ?")) {

            putToken.setInt(1, id);
            putToken.execute();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void dropTokenById(String token) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement putToken = connection.prepareStatement("DELETE FROM matcha.JWT WHERE TOKEN = ?")) {

            putToken.setString(1, token);
            putToken.execute();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
