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
    public List<ChatAffiliation> getByUserId(int id) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(),config.getUser(), config.getPassword());
             PreparedStatement state = connection.prepareStatement("SELECT * FROM matcha.CHAT_AFFILIATION where USER_ID = ?"))
        {
            state.setInt(1, id);
            state.execute();

            try (ResultSet resultSet = state.getResultSet()) {
                LinkedList<ChatAffiliation> resultList = new LinkedList<>();
                while (resultSet.next()) {
                    ChatAffiliation chatAffiliation = new ChatAffiliation();
                    chatAffiliation.setId(resultSet.getInt("ID"));
                    chatAffiliation.setCreationTime(resultSet.getTime("CREATION_TIME"));
                    chatAffiliation.setUserId(resultSet.getInt("USER_ID"));
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
