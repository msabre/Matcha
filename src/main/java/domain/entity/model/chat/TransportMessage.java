package domain.entity.model.chat;

import domain.entity.Message;

public class TransportMessage {
    private Message message;
    private GetMessageRq getMessageRq;
    private MessageNotification messageNotification;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public GetMessageRq getGetMessageRq() {
        return getMessageRq;
    }

    public void setGetMessageRq(GetMessageRq getMessageRq) {
        this.getMessageRq = getMessageRq;
    }

    public MessageNotification getMessageNotification() {
        return messageNotification;
    }

    public void setMessageNotification(MessageNotification messageNotification) {
        this.messageNotification = messageNotification;
    }
}
