package application.services.json.typeAdapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import domain.entity.model.types.SexualPreferenceType;

import java.lang.reflect.Type;

public class SexualPreferenceTypeDeserializer implements JsonDeserializer<SexualPreferenceType> {

    @Override
    public SexualPreferenceType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String value = jsonElement.getAsString();
        return SexualPreferenceType.fromStr(value);
    }
}
