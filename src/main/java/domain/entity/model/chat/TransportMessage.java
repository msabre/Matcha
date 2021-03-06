package domain.entity.model.chat;

import domain.entity.Message;

import java.util.List;

public class TransportMessage {
    private Message message;
    private List<Message> messageAnswer;
    private GetMessageRq getMessageRq;
    private DeleteMessage deleteMessage;
    private MessageNotification messageNotification;
    private DeliveryNotification deliveryNotification;
    private int chatId;

    public Message getMessage() {
        return message;
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

    public DeleteMessage getDeleteMessage() {
        return deleteMessage;
    }

    public void setMessageNotification(MessageNotification messageNotification) {
        this.messageNotification = messageNotification;
    }

    public DeliveryNotification getDeliveryNotification() {
        return deliveryNotification;
    }

    public MessageNotification getMessageNotification() {
        return messageNotification;
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
        private int specificId;
        private int[] messageIds;

        private GetMessageRqType type;

        public int[] getMessageIds() {
            return messageIds;
        }

        public int getSpecificId() {
            return specificId;
        }

        public GetMessageRqType getType() {
            return type;
        }

        public void setType(GetMessageRqType type) {
            this.type = type;
        }

        public enum GetMessageRqType {
            GET_FIRST_PACK,
            AFTER_LAST,
            BEFORE_FIRST,
            BY_IDS
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

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }
}
