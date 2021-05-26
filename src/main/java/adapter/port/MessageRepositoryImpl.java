package adapter.port;

import adapter.port.model.DBConfiguration;
import domain.entity.model.WebSocketMessage;
import usecase.port.MessageRepository;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageRepositoryImpl implements MessageRepository {
    private static MessageRepositoryImpl instance;


    private final DBConfiguration config = DBConfiguration.getConfig();

    private MessageRepositoryImpl() {

    }

    public static MessageRepositoryImpl getRepository() {
        if (instance == null) {
            instance = new MessageRepositoryImpl();
        }

        return instance;
    }

    @Override
    public void save(WebSocketMessage msg) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement state = connection.prepareStatement("INSERT INTO TABLE match.web_socket_message(chat_id," +
                     "from_id, type_id, content) VALUES(?, ?, ?, ?)")) {
            state.setInt(1, msg.getChatId());
            state.setInt(2, msg.getFromId());
            state.setString(3, msg.getType());
            state.setBlob(4, new ByteArrayInputStream(msg.getContent().getBytes()));

            state.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearAllById(int chatId) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement state = connection.prepareStatement("DELETE FROM matcha.web_socket_message WHERE chatId = ?")) {
            state.setInt(1, chatId);
            state.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<WebSocketMessage> getList(int chatId) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement state = connection.prepareStatement("SELECT * FROM matcha.web_socket_message WHERE chat_id =?")) {
            state.setInt(1, chatId);
            state.execute();

            ResultSet resultSet = state.getResultSet();
            List<WebSocketMessage> history = new ArrayList<>(30);
            while (resultSet.next()) {
                WebSocketMessage msg = new WebSocketMessage(resultSet.getInt(1), resultSet.getInt(2),
                        resultSet.getString(3), resultSet.getString(4));
                history.add(msg);
            }
            return history;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
