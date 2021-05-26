package usecase.port;

import domain.entity.model.WebSocketMessage;

import java.util.List;

public interface MessageRepository {
    void save(WebSocketMessage msg);

    void clearAllById(int chatId);

    List<WebSocketMessage> getList(int chatId);
}
