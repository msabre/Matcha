package adapter.controller;

import domain.entity.model.WebSocketMessage;
import usecase.ClearAllMessages;
import usecase.ReturnAllMessages;
import usecase.SaveMessage;

import java.util.List;

public class MessageController {
    private final SaveMessage saveMessage;
    private final ClearAllMessages clearAllMessages;
    private final ReturnAllMessages returnAllMessages;

    public MessageController(SaveMessage saveMessage, ClearAllMessages clearAllMessages, ReturnAllMessages returnAllMessages) {
        this.saveMessage = saveMessage;
        this.clearAllMessages = clearAllMessages;
        this.returnAllMessages = returnAllMessages;
    }

    public void save(WebSocketMessage msg) {
        saveMessage.save(msg);
    }

    public void clearAll(int chatId) {
        clearAllMessages.clear(chatId);
    }

    public List<WebSocketMessage> getAll(int chatId) {
        return returnAllMessages.get(chatId);
    }
}
