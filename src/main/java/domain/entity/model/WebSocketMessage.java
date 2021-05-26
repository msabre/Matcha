package domain.entity.model;

public class WebSocketMessage {
    private int chatId;
    private int fromId;
    private String type;
    private String content;


    public WebSocketMessage(int chatId, int fromId, String type, String content) {
        this.chatId = chatId;
        this.fromId = fromId;
        this.type = type;
        this.content = content;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
