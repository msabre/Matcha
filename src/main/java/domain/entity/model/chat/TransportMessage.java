package domain.entity.model.chat;

import domain.entity.Message;

import java.util.List;

public class TransportMessage {
    private Integer chatId;

    private Error error;
    private Message message;
    private List<Message> messageAnswer;
    private GetMessageRq getMessageRq;
    private DeleteMessage deleteMessage;
    private MessageNotification messageNotification;

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
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

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
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
        private int chatId;
        private int lastId;
        private int[] messageIds;

        private String type;

        public int getChatId() {
            return chatId;
        }

        public void setChatId(int chatId) {
            this.chatId = chatId;
        }

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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    /**
     * Сообщение о ошибке
     * */
    public static class Error {
        private String errorText;

        public Error(String errorText) {
            this.errorText = errorText;
        }

        public String getErrorText() {
            return errorText;
        }

        public void setErrorText(String errorText) {
            this.errorText = errorText;
        }
    }

    /**
     * Заявка на удаление сообщения
     * */
    public static class DeleteMessage {
        private int chatId;
        private int[] ids;

        public int getChatId() {
            return chatId;
        }

        public void setChatId(int chatId) {
            this.chatId = chatId;
        }

        public int[] getIds() {
            return ids;
        }

        public void setIds(int[] ids) {
            this.ids = ids;
        }
    }
}
