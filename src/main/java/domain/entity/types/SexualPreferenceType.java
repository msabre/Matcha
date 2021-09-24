package domain.entity.types;

public enum SexualPreferenceType {
    GETERO("getero"),
    GAY("gay"),
    LESBY("lesbi"),
    BISEXUAL("bisexual");

    private String value;

    SexualPreferenceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static SexualPreferenceType fromStr(String value) {
        for (SexualPreferenceType sexualPreferenceType : SexualPreferenceType.values()) {
            if (sexualPreferenceType.getValue().equals(value))
                return sexualPreferenceType;
        }
        return null;
    }
}
