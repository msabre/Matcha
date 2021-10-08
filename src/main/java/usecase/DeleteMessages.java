package usecase;

import usecase.port.MessageRepository;

public class DeleteMessages {
    private final MessageRepository repository;

    public DeleteMessages(MessageRepository repository) {
        this.repository = repository;
    }

    public void delete(int chatId, int...ids) {
        repository.deleteNByIds(chatId, ids);
    }
}
