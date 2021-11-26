package domain.entity.model.chat;

import domain.entity.JsonWebToken;

import javax.websocket.Session;

public class ChatUser {
    private int userId;
    private Session session;
    private JsonWebToken jwt;

    public ChatUser(int userId, Session session, JsonWebToken jwt) {
        this.userId = userId;
        this.session = session;
        this.jwt = jwt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public JsonWebToken getJwt() {
        return jwt;
    }

    public void setJwt(JsonWebToken jwt) {
        this.jwt = jwt;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
