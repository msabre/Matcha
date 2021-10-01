package domain.entity.model.chat;

public class GetMessageRq {
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
