package domain.entity.model.types;

public enum Action {
    LIKE("LIKE"),
    DISLIKE("DISLIKE"),
    TAKE_LIKE("TAKELIKE"),
    MATCH("MATCH"),
    VISIT("VISIT");

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
