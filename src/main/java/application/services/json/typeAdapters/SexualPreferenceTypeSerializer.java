package application.services.json.typeAdapters;

import com.google.gson.*;
import domain.entity.model.types.SexualPreferenceType;

import java.lang.reflect.Type;

public class SexualPreferenceTypeSerializer implements JsonSerializer<SexualPreferenceType> {

    @Override
    public JsonElement serialize(SexualPreferenceType sexualPreferenceType, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(sexualPreferenceType.getValue());
    }
}
