package domain.entity.types;

public enum MessageType {
    TEXT("text"),
    IMAGE("image");

    private final String value;

    MessageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MessageType fromStr(String value) {
        for (MessageType type : MessageType.values()) {
            if (type.getValue().equals(value))
                return type;
        }
        return null;
    }
}
