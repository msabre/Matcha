package usecase.port;

import domain.entity.Message;

import java.util.List;

public interface MessageRepository {
    Message save(Message msg);

    void clearAllById(int chatId);

    List<Message> getFirstNMatches(int chatId, int size);

    List<Message> getListOfNSizeAfterSpecificId(int chatId, int messageId, int size);

    List<Message> getNByIds(int chatId, int...ids);
}
