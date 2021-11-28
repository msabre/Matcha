package usecase;

import domain.entity.Message;
import usecase.port.MessageRepository;

import java.util.List;

public class GetMessages {
    private final MessageRepository repository;

    public GetMessages(MessageRepository repository) {
        this.repository = repository;
    }

    public List<Message> getFirstNMatches(int chatId, int userId, int size) {
        return repository.getFirstNMatches(chatId, userId, size);
    }

    public List<Message> getListOfNSizeBeforeSpecificId(int chatId, int userId, int messageId, int size) {
        return repository.getListOfNSizeBefore1SpecificId(chatId, userId, messageId, size);
    }

    public List<Message> getListOfNSizeAfterSpecificId(int chatId, int userId, int messageId, int size) {
        return repository.getListOfNSizeAfter1SpecificId(chatId, userId, messageId, size);
    }

    public List<Message> getNByIds(int chatId, int...ids) {
        return repository.getNByIds(chatId, ids);
    }
}
