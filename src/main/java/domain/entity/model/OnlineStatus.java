package domain.entity.model;

import java.time.ZonedDateTime;

public class OnlineStatus {
    private int userId;
    private Status status;
    private ZonedDateTime lastAction;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ZonedDateTime getLastAction() {
        return lastAction;
    }

    public void setLastAction(ZonedDateTime lastAction) {
        this.lastAction = lastAction;
    }

    public enum Status {
        ONLINE,
        OFFLINE;

        public static Status fromString(String value) {
            if (value == null)
                return null;
            for (Status status : Status.values()) {
                if (status.toString().equals(value)) {
                    return status;
                }
            }
            return null;
        }
    }
}
