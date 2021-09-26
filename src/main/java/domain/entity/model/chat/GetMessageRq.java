package domain.entity.model.chat;

public class GetMessageRq {
    private int chatId;
    private int[] messageIds;

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
}
