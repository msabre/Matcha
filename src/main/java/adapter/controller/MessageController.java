package adapter.controller;

import domain.entity.Message;
import usecase.ClearAllMessages;
import usecase.GetMessages;
import usecase.SaveMessage;

import java.util.List;

public class MessageController {
    private final SaveMessage saveMessage;
    private final ClearAllMessages clearAllMessages;
    private final GetMessages getMessages;

    public MessageController(SaveMessage saveMessage, ClearAllMessages clearAllMessages, GetMessages getMessages) {
        this.saveMessage = saveMessage;
        this.clearAllMessages = clearAllMessages;
        this.getMessages = getMessages;
    }

    public Message save(Message msg) {
        return saveMessage.save(msg);
    }

    public void clearAll(int chatId) {
        clearAllMessages.clear(chatId);
    }

    public List<Message> getFirstNMatches(int chatId, int size) {
        return getMessages.getFirstNMatches(chatId, size);
    }

    public List<Message> getNByIds(int chatId, int...ids) {
        return getMessages.getNByIds(chatId, ids);
    }
}
