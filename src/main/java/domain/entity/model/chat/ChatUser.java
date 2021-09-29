package domain.entity.model.chat;

import javax.websocket.Session;

public class ChatUser {
    private int userId;
    private int chatId;
    private Session session;

    public ChatUser(int userId, int chatId, Session session) {
        this.userId = userId;
        this.chatId = chatId;
        this.session = session;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}