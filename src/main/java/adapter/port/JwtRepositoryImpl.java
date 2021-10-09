package adapter.port;

import adapter.port.model.DBConfiguration;
import domain.entity.JsonWebToken;
import domain.entity.model.types.JwtType;
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
    public boolean putToken(JsonWebToken token, JwtType type) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
            PreparedStatement putToken = connection.prepareStatement("INSERT  INTO matcha.JWT(USER_ID, TOKEN, TYPE) VALUES(?, ?, ?)")) {

            putToken.setString(2, token.getToken());
            putToken.setInt(1, token.getUserId());
            putToken.setString(3, type.toString());
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
                if (rs.next())
                    return rs.getInt("ID");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void dropToken(Integer id) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement putToken = connection.prepareStatement("DELETE FROM matcha.JWT WHERE USER_ID = ?")) {

            putToken.setInt(1, id);
            putToken.execute();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void dropToken(String token) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement putToken = connection.prepareStatement("DELETE FROM matcha.JWT WHERE TOKEN = ?")) {

            putToken.setString(1, token);
            putToken.execute();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dropTokenByUserId(Integer userId, JwtType type) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement putToken = connection.prepareStatement("DELETE FROM matcha.JWT WHERE USER_ID = ? AND TYPE = ?")) {

            putToken.setInt(1, userId);
            putToken.setString(2, type.toString());
            putToken.execute();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
