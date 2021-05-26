package usecase;

import domain.entity.model.WebSocketMessage;
import usecase.port.MessageRepository;

public class SaveMessage {
    private final MessageRepository repository;

    public SaveMessage(MessageRepository repository) {
        this.repository = repository;
    }

    public void save(WebSocketMessage msg) {
        repository.save(msg);
    }
}
