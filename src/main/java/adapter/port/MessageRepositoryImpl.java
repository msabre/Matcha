package adapter.port;

import adapter.port.model.DBConfiguration;
import domain.entity.Message;
import domain.entity.model.types.MessageStatus;
import domain.entity.model.types.MessageType;
import usecase.port.MessageRepository;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

public class MessageRepositoryImpl implements MessageRepository {
    private static MessageRepositoryImpl instance;


    private final DBConfiguration config = DBConfiguration.getConfig();

    private MessageRepositoryImpl() {}

    public static MessageRepositoryImpl getRepository() {
        if (instance == null) {
            instance = new MessageRepositoryImpl();
        }

        return instance;
    }

    @Override
    public Message save(Message msg) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement state = connection.prepareStatement(
                     "INSERT INTO matcha.web_socket_message(chat_id, from_id, to_id, web_socket_message.type, status, content) VALUES(?, ?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            int i = 1;
            state.setInt(i++, msg.getChatId());
            state.setInt(i++, msg.getFromId());
            state.setInt(i++, msg.getToId());
            state.setString(i++, msg.getType().getValue());
            state.setString(i++, msg.getStatus().getValue());
            state.setBlob(i, new ByteArrayInputStream(msg.getContent().getBytes()));

            state.execute();

            ResultSet resultSet = state.getGeneratedKeys();
            if (resultSet.next())
                msg.setId(resultSet.getInt(1));
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return msg;
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
             PreparedStatement state = connection.prepareStatement("SELECT * FROM matcha.web_socket_message msg WHERE msg.chat_id = ? ORDER BY msg.CREATION_TIME DESC LIMIT ?")) {
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
        return Collections.emptyList();
    }

    @Override
    public List<Message> getListOfNSizeAfterSpecificId(int chatId, int messageId, int size) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement state = connection.prepareStatement("WITH lastIdTime as (SELECT msg.CREATION_TIME FROM matcha.web_socket_message msg WHERE msg.ID = ?) " +
                     "SELECT * FROM matcha.web_socket_message msg, lastIdTime tm WHERE msg.chat_id = ? AND msg.CREATION_TIME < tm.CREATION_TIME ORDER BY msg.CREATION_TIME DESC LIMIT ?")) {
            int i = 1;
            state.setInt(i++, messageId);
            state.setInt(i++, chatId);
            state.setInt(i, size);
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
        return Collections.emptyList();
    }

    public List<Message> getNByIds(int chatId, int...ids) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement state = connection.prepareStatement(
                     "SELECT * FROM matcha.web_socket_message msg WHERE msg.chat_id = ? AND FIND_IN_SET(msg.ID, ?) > 0")) {

            String idsLine = Arrays.stream(ids).mapToObj(String::valueOf).collect(Collectors.joining(","));
            state.setInt(1, chatId);
            state.setString(2, idsLine);
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
        return Collections.emptyList();
    }

    private Message getMessageFromResultSet(ResultSet resultSet) throws SQLException {
        Message msg = new Message();
        msg.setId(resultSet.getInt("ID"));
        msg.setCreationTime(new Date(resultSet.getTimestamp("CREATION_TIME").getTime()));
        msg.setChatId(resultSet.getInt("CHAT_ID"));
        msg.setFromId(resultSet.getInt("FROM_ID"));
        msg.setToId(resultSet.getInt("TO_ID"));
        msg.setType(MessageType.fromStr(resultSet.getString("TYPE")));
        msg.setStatus(MessageStatus.fromStr(resultSet.getString("STATUS")));

        Blob content = resultSet.getBlob("CONTENT");
        msg.setContent(new String(content.getBytes(1, (int) content.length()), StandardCharsets.UTF_8));
        return msg;
    }
}
