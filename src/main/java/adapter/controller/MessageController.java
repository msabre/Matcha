package adapter.controller;

import config.MyConfiguration;
import domain.entity.Message;
import usecase.ClearAllMessages;
import usecase.GetMessages;
import usecase.SaveMessage;

import java.util.List;

public class MessageController {
    private static MessageController instance;

    private SaveMessage saveMessage;
    private ClearAllMessages clearAllMessages;
    private GetMessages getMessages;

    private MessageController() {
    }

    public static MessageController getController() {
        if (instance == null) {
            instance = new MessageController();

            instance.saveMessage = MyConfiguration.saveMessage();
            instance.clearAllMessages = MyConfiguration.clearAllMessages();
            instance.getMessages = MyConfiguration.getMessages();
        }

        return instance;
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

    public List<Message> getListOfNSizeAfterSpecificId(int chatId, int lastMessageId, int size) {
        return getMessages.getListOfNSizeAfterSpecificId(chatId, lastMessageId, size);
    }
}
