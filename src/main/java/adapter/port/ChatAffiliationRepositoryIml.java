package adapter.port;

import adapter.port.model.DBConfiguration;
import domain.entity.ChatAffiliation;
import domain.entity.FilterParams;
import domain.entity.model.types.Action;
import usecase.port.ChatAffiliationRepository;
import usecase.port.LikesActionRepository;

import java.sql.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatAffiliationRepositoryIml implements ChatAffiliationRepository {

    private static ChatAffiliationRepositoryIml instance;

    private final DBConfiguration config = DBConfiguration.getConfig();

    private ChatAffiliationRepositoryIml() {}

    public static ChatAffiliationRepositoryIml getRepository() {
        if (instance == null) {
            instance = new ChatAffiliationRepositoryIml();
        }

        return instance;
    }

    @Override
    public int getChatMaxId() {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT MAX(CHAT_ID) FROM matcha.CHAT_AFFILIATION")) {
            statement.execute();

            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean create(int from_usr, int to_usr, int chatId) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO matcha.CHAT_AFFILIATION(FROM_USR, TO_USR, CHAT_ID) VALUES (?, ?, ?)")) {
            statement.setInt(1, from_usr);
            statement.setInt(2, to_usr);
            statement.setInt(3, chatId);
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(int chatId) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE matcha.CHAT_AFFILIATION chat where chat.CHAT_ID = ?")) {
            statement.setInt(1, chatId);
            statement.execute();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<ChatAffiliation> getByIdsWithToUsr(List<Integer> ids, int toUsr) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement state = connection.prepareStatement("SELECT * FROM matcha.CHAT_AFFILIATION chat where FIND_IN_SET(chat.FROM_USR, ?) > 0 AND TO_USR = ?"))
        {
            String idsLine = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
            state.setString(1, idsLine);
            state.setInt(2, toUsr);
            state.execute();

            try (ResultSet resultSet = state.getResultSet()) {
                LinkedList<ChatAffiliation> resultList = new LinkedList<>();
                while (resultSet.next()) {
                    ChatAffiliation chatAffiliation = new ChatAffiliation();
                    chatAffiliation.setId(resultSet.getInt("ID"));
                    chatAffiliation.setCreationTime(resultSet.getTime("CREATION_TIME"));
                    chatAffiliation.setFromUsr(resultSet.getInt("FROM_USR"));
                    chatAffiliation.setToUsr(resultSet.getInt("TO_USR"));
                    chatAffiliation.setChatId(resultSet.getInt("CHAT_ID"));

                    resultList.add(chatAffiliation);
                }
                return resultList;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
