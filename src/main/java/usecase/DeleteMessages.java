package usecase;

import usecase.port.MessageRepository;

public class DeleteMessages {
    private final MessageRepository repository;

    public DeleteMessages(MessageRepository repository) {
        this.repository = repository;
    }

    public void deleteNByIdsForUser(int chatId, int userId, int...ids) {
        repository.deleteNByIdsForUser(chatId, userId, ids);
    }

    public void deleteAllByUserId(int chatId, int userId) {
        repository.deleteAllByUserId(chatId, userId);
    }
}
