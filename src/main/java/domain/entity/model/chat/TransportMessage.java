package domain.entity.model.chat;

import domain.entity.Message;

import java.util.List;

public class TransportMessage {
    private Message message;
    private List<Message> messageAnswer;
    private GetMessageRq getMessageRq;
    private MessageNotification messageNotification;

    public List<Message> getMessageAnswer() {
        return messageAnswer;
    }

    public void setMessageAnswer(List<Message> messageAnswer) {
        this.messageAnswer = messageAnswer;
    }

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
