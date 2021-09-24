package adapter.port;

import adapter.port.model.DBConfiguration;
import domain.entity.Message;
import domain.entity.types.MessageStatus;
import domain.entity.types.MessageType;
import usecase.port.MessageRepository;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.LinkedList;
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
    public void save(Message msg) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement state = connection.prepareStatement("INSERT INTO TABLE match.web_socket_message(chat_id, from_id, to_id, type, status, content) VALUES(?, ?, ?, ?, ?, ?)")) {
            int i = 1;
            state.setInt(i++, msg.getChatId());
            state.setInt(i++, msg.getFromId());
            state.setInt(i++, msg.getToId());
            state.setString(i++, msg.getType().getValue());
            state.setString(i++, msg.getStatus().getValue());
            state.setBlob(i, new ByteArrayInputStream(msg.getContent()));

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
    public List<Message> getFirstNMatches(int chatId, int size) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement state = connection.prepareStatement("SELECT * FROM matcha.web_socket_message WHERE chat_id = ? AND LIMIT ?")) {
            state.setInt(1, chatId);
            state.setInt(2, size);
            state.execute();

            ResultSet resultSet = state.getResultSet();
            List<Message> history = new LinkedList<>();
            while (resultSet.next())
                history.add(getMessageFromResultSet(resultSet));
            return history;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Message> getListOfNSizeAfterSpecificId(int chatId, int messageId, int size) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement state = connection.prepareStatement("SELECT * FROM matcha.web_socket_message WHERE chat_id = ? AND ID > ? LIMIT ?")) {
            state.setInt(1, chatId);
            state.setInt(2, messageId);
            state.setInt(3, size);
            state.execute();

            ResultSet resultSet = state.getResultSet();
            List<Message> history = new LinkedList<>();
            while (resultSet.next())
                history.add(getMessageFromResultSet(resultSet));
            return history;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Message getMessageFromResultSet(ResultSet resultSet) throws SQLException {
        Message msg = new Message();
        msg.setId(resultSet.getInt("ID"));
        msg.setChatId(resultSet.getInt("CHAT_ID"));
        msg.setFromId(resultSet.getInt("FROM_ID"));
        msg.setToId(resultSet.getInt("TO_ID"));
        msg.setType(MessageType.fromStr(resultSet.getString("TYPE")));
        msg.setStatus(MessageStatus.fromStr(resultSet.getString("STATUS")));

        Blob content = resultSet.getBlob("CONTENT");
        msg.setContent(resultSet.getBlob("CONTENT").getBytes(0, (int) content.length()));
        return msg;
    }
}
