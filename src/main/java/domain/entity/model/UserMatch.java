package domain.entity.model;

import domain.entity.Photo;

public class UserMatch {
    private int userId;
    private Integer chatId;
    private Photo icon;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public Photo getIcon() {
        return icon;
    }

    public void setIcon(Photo icon) {
        this.icon = icon;
    }
}
