package adapter.controller;

import config.MyConfiguration;
import domain.entity.Message;
import usecase.ChatDelete;
import usecase.DeleteMessages;
import usecase.GetMessages;
import usecase.SaveMessage;

import java.util.List;

public class MessageController {
    private static MessageController instance;

    private SaveMessage saveMessage;
    private GetMessages getMessages;
    private DeleteMessages deleteMessages;

    private MessageController() {
    }

    public static MessageController getController() {
        if (instance == null) {
            instance = new MessageController();

            instance.saveMessage = MyConfiguration.saveMessage();
            instance.getMessages = MyConfiguration.getMessages();
        }

        return instance;
    }

    public Message save(Message msg) {
        return saveMessage.save(msg);
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

    public void deleteNByIds(int chatId, int...ids) {
        deleteMessages.delete(chatId, ids);
    }
}
