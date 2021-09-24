package domain.entity.types;

public enum GenderType {
    MALE("male"),
    FEMALE("female");

    private final String value;

    GenderType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return super.toString();
    }

     public static GenderType fromStr(String value) {
            for (GenderType genderType : GenderType.values()) {
            if (genderType.getValue().equals(value))
                return genderType;
        }
        return null;
    }
}
