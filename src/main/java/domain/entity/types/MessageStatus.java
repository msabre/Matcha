package domain.entity.types;

public enum MessageStatus {
    RECEIVED(""),
    DELIVERED("");

    private final String value;

    MessageStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MessageStatus fromStr(String value) {
        for (MessageStatus status : MessageStatus.values()) {
            if (status.getValue().equals(value))
                return status;
        }
        return null;
    }
}
