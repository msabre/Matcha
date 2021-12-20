package domain.entity.model.types;

public enum CityType {
    MOSCOW("Moscow"),
    SAINT_PETERSBURG("Saint-Petersburg"),
    SAMARA("Samara"),
    UFA("Ufa"),
    KAZAN("Kazan"),
    YEKATERINBURG("Yekaterinburg");
    
    private final String value;

    CityType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    public static CityType fromValue(String value) {
        if (value == null)
            return null;
        for (CityType type : CityType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }
}
