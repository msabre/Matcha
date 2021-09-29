package domain.entity.model.types;

public enum MessageStatus {
    RECEIVED("RECEIVED"),
    DELIVERED("DELIVERED");

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
