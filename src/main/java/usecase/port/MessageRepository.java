package usecase.port;

import domain.entity.Message;

import java.util.List;

public interface MessageRepository {
    Message save(Message msg);

    List<Message> getFirstNMatches(int chatId, int userId, int size);

    List<Message> getListOfNSizeAfterSpecificId(int chatId, int userId, int messageId, int size);

    List<Message> getNByIds(int chatId, int...ids);

    void deleteNByIdsForUser(int chatId, int userId, int...ids);

    boolean deleteAllByUserId(int chatId, int userId);

    boolean markAsRead(int...messageIds);
}
