package domain.entity.model.types;

public enum Action {
    LIKE("LIKE"),
    DISLIKE("DISLIKE"),
    TAKE_LIKE("TAKE_LIKE"),
    MATCH("MATCH"),
    PAIR("PAIR"),
    VISIT("VISIT"),
    BLOCK("BLOCK"),
    FAKE("FAKE"),
    TAKE_FAKE("TAKE_FAKE");

    private final String value;

    Action(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
