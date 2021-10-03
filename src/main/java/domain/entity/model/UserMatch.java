package domain.entity.model;

import com.google.gson.annotations.Expose;
import domain.entity.Photo;

public class UserMatch {
    @Expose private int id;
    @Expose private int userId;
    @Expose private Integer chatId;
    @Expose private Photo icon;

    public int getMatchId() {
        return id;
    }

    public void setMatchId(int matchId) {
        this.id = matchId;
    }

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
