package usecase;

import domain.entity.Message;
import usecase.port.MessageRepository;

import java.util.List;

public class GetMessages {
    private final MessageRepository repository;

    public GetMessages(MessageRepository repository) {
        this.repository = repository;
    }

    public List<Message> getFirstNMatches(int chatId, int size) {
        return repository.getFirstNMatches(chatId, size);
    }

    public List<Message> getListOfNSizeAfterSpecificId(int chatId, int messageId, int size) {
        return repository.getListOfNSizeAfterSpecificId(chatId, messageId, size);
    }
}
