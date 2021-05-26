package usecase;

import usecase.port.MessageRepository;

public class ClearAllMessages {
    private final MessageRepository repository;

    public ClearAllMessages(MessageRepository repository) {
        this.repository = repository;
    }

    public void clear(Integer chatId) {
        repository.clearAllById(chatId);
    }
}
