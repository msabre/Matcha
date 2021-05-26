package usecase;

import domain.entity.model.WebSocketMessage;
import usecase.port.MessageRepository;

import java.util.List;

public class ReturnAllMessages {
    private final MessageRepository repository;

    public ReturnAllMessages(MessageRepository repository) {
        this.repository = repository;
    }

    public List<WebSocketMessage> get(int chatId) {
        return repository.getList(chatId);
    }
}
