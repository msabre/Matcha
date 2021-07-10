package domain.entity;

public class JsonWebToken {

    private int id;
    private String token;
    private String userFingerprint;
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
}
