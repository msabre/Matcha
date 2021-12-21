package application.services.json.typeAdapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZoneDateTimeTypeSerializer implements JsonSerializer<ZonedDateTime> {

    @Override
    public JsonElement serialize(ZonedDateTime zonedDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
        String value = DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss").format(zonedDateTime);
        return new JsonPrimitive(value);
    }
}
