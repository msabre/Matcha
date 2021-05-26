package domain.entity;

import javax.crypto.SecretKey;

public class JsonWebToken {

    private int id;
    private String token;
    private String userFingerprint;
    private SecretKey key;
    private int userId;

    public String getUserFingerprint() {
        return userFingerprint;
    }

    public void setUserFingerprint(String userFingerprint) {
        this.userFingerprint = userFingerprint;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public SecretKey getKey() {
        return key;
    }

    public void setKey(SecretKey key) {
        this.key = key;
    }
}
