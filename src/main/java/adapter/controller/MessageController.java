package adapter.controller;

import config.MyConfiguration;
import domain.entity.Message;
import usecase.*;

import java.util.List;

public class MessageController {
    private static MessageController instance;

    private SaveMessage saveMessage;
    private GetMessages getMessages;
    private DeleteMessages deleteMessages;
    private MarkAsRead markAsRead;

    private MessageController() {
    }

    public static MessageController getController() {
        if (instance == null) {
            instance = new MessageController();

            instance.saveMessage = MyConfiguration.saveMessage();
            instance.getMessages = MyConfiguration.getMessages();
            instance.deleteMessages = MyConfiguration.deleteMessage();
            instance.markAsRead = MyConfiguration.markAsRead();
        }

        return instance;
    }

    public Message save(Message msg) {
        return saveMessage.save(msg);
    }

    public List<Message> getFirstNMatches(int chatId, int userId, int size) {
        return getMessages.getFirstNMatches(chatId, userId, size);
    }

    public List<Message> getNByIds(int chatId, int...ids) {
        return getMessages.getNByIds(chatId, ids);
    }

    public List<Message> getListOfNSizeAfterSpecificId(int chatId, int userId, int lastMessageId, int size) {
        return getMessages.getListOfNSizeAfterSpecificId(chatId, userId, lastMessageId, size);
    }

    public List<Message> getListOfNSizeBeforeSpecificId(int chatId, int userId, int firstMessageId, int size) {
        return getMessages.getListOfNSizeBeforeSpecificId(chatId, userId, firstMessageId, size);
    }

    public void deleteByIdsForUser(int chatId, int userId, int...ids) {
        deleteMessages.deleteNByIdsForUser(chatId, userId, ids);
    }

    public boolean markAsRead(int...ids) {
        return markAsRead.markAsRead(ids);
    }

    public void deleteAllForUser(int chatId, int userId) {
        deleteMessages.deleteAllByUserId(chatId, userId);
    }
}
