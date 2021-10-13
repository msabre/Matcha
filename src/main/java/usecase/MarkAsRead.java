package usecase;

import usecase.port.MessageRepository;

public class MarkAsRead {
    private final MessageRepository repository;

    public MarkAsRead(MessageRepository repository) {
        this.repository = repository;
    }

    public boolean markAsRead(int...messageIds) {
        return repository.markAsRead(messageIds);
    }
}
