package application.services.json.typeAdapters;

import com.google.gson.*;
import domain.entity.model.types.GenderType;

import java.lang.reflect.Type;

public class GenderTypeSerializer implements JsonSerializer<GenderType> {

    @Override
    public JsonElement serialize(GenderType genderType, Type type, JsonSerializationContext jsonSerializationContext) {
        String value = genderType.getValue();
        return new JsonPrimitive(value);
    }
}
