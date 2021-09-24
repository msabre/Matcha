package usecase;

import domain.entity.Message;
import usecase.port.MessageRepository;

public class SaveMessage {
    private final MessageRepository repository;

    public SaveMessage(MessageRepository repository) {
        this.repository = repository;
    }

    public void save(Message msg) {
        repository.save(msg);
    }
}
