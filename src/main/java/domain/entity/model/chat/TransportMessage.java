package domain.entity.model.chat;

import domain.entity.Message;

import java.util.List;

public class TransportMessage {
    private Answer answer;
    private Message message;
    private List<Message> messageAnswer;
    private GetMessageRq getMessageRq;
    private DeleteMessage deleteMessage;
    private MessageNotification messageNotification;
    private DeliveryNotification deliveryNotification;

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public List<Message> getMessageAnswer() {
        return messageAnswer;
    }

    public void setMessageAnswer(List<Message> messageAnswer) {
        this.messageAnswer = messageAnswer;
    }

    public GetMessageRq getGetMessageRq() {
        return getMessageRq;
    }

    public void setGetMessageRq(GetMessageRq getMessageRq) {
        this.getMessageRq = getMessageRq;
    }

    public DeleteMessage getDeleteMessage() {
        return deleteMessage;
    }

    public void setDeleteMessage(DeleteMessage deleteMessage) {
        this.deleteMessage = deleteMessage;
    }

    public MessageNotification getMessageNotification() {
        return messageNotification;
    }

    public void setMessageNotification(MessageNotification messageNotification) {
        this.messageNotification = messageNotification;
    }

    public DeliveryNotification getDeliveryNotification() {
        return deliveryNotification;
    }

    public void setDeliveryNotification(DeliveryNotification deliveryNotification) {
        this.deliveryNotification = deliveryNotification;
    }

    /**
     * Уведомление о полученном сообщении
     * */
    public static class MessageNotification {
        private int messageId;
        private int senderId;

        public int getMessageId() {
            return messageId;
        }

        public void setMessageId(int messageId) {
            this.messageId = messageId;
        }

        public int getSenderId() {
            return senderId;
        }

        public void setSenderId(int senderId) {
            this.senderId = senderId;
        }
    }

    /**
     * Заявка на получение сообщения(й)
     * */
    public static class GetMessageRq {
        private int lastId;
        private int[] messageIds;

        private GetMessageRqType type;

        public int[] getMessageIds() {
            return messageIds;
        }

        public void setMessageIds(int[] messageIds) {
            this.messageIds = messageIds;
        }

        public int getLastId() {
            return lastId;
        }

        public void setLastId(int lastId) {
            this.lastId = lastId;
        }

        public GetMessageRqType getType() {
            return type;
        }

        public void setType(GetMessageRqType type) {
            this.type = type;
        }

        public enum GetMessageRqType {
            AFTER_LAST,
            BY_IDS
        }
    }

    /**
     * Сообщение о ошибке
     * */
    public static class Answer {
        private String text;

        public Answer(String errorText) {
            this.text = errorText;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    /**
     * Заявка на удаление сообщения
     * */
    public static class DeleteMessage {
        private int[] ids;
        private DeleteMessageType type;

        public DeleteMessageType getType() {
            return type;
        }

        public void setType(DeleteMessageType type) {
            this.type = type;
        }

        public int[] getIds() {
            return ids;
        }

        public void setIds(int[] ids) {
            this.ids = ids;
        }

        public enum DeleteMessageType {
            ALL,
            BY_IDS
        }
    }

    /**
     * Уведомление о прочитанных сообщениях
     * */
    public static class DeliveryNotification {
        private int[] ids;

        public int[] getIds() {
            return ids;
        }

        public void setIds(int[] ids) {
            this.ids = ids;
        }
    }
}
