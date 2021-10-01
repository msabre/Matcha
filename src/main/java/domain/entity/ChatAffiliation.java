package domain.entity;

import java.util.Date;

public class ChatAffiliation {
    private int id;
    private Date creationTime;
    private int from_urs;
    private int to_usr;
    private int chatId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public int getFrom_urs() {
        return from_urs;
    }

    public void setFrom_urs(int from_urs) {
        this.from_urs = from_urs;
    }

    public int getTo_usr() {
        return to_usr;
    }

    public void setTo_usr(int to_usr) {
        this.to_usr = to_usr;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }
}
