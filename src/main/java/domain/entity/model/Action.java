package domain.entity.model;

public enum Action {
    LIKE("LIKE"),
    DISLIKE("DISLIKE"),
    MATCH("MATCH");

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
