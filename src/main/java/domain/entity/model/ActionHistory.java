package domain.entity.model;

import com.google.gson.annotations.Expose;
import domain.entity.Photo;

public class ActionHistory {
    @Expose private int userId;
    @Expose private Integer chatId;
    @Expose private Photo icon;
    @Expose private String firstName;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
