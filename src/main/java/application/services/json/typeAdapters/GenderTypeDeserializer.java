package application.services.json.typeAdapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import domain.entity.model.types.GenderType;

import java.lang.reflect.Type;

public class GenderTypeDeserializer implements JsonDeserializer<GenderType> {

    @Override
    public GenderType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String value = jsonElement.getAsString();
        return GenderType.fromStr(value);
    }
}
