package application.services.json;

import application.services.json.typeAdapters.*;
import com.google.gson.*;

import com.google.gson.reflect.TypeToken;
import domain.entity.Photo;
import domain.entity.model.types.GenderType;
import domain.entity.model.types.SexualPreferenceType;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

public class JsonService {

    private static Gson gsonBuilder;

    private static Gson getGsonBuilder() {
        if (gsonBuilder == null) {
            gsonBuilder = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(GenderType.class, new GenderTypeSerializer())
                .registerTypeAdapter(GenderType.class, new GenderTypeDeserializer())
                .registerTypeAdapter(SexualPreferenceType.class, new SexualPreferenceTypeSerializer())
                .registerTypeAdapter(SexualPreferenceType.class, new SexualPreferenceTypeDeserializer())
                .registerTypeAdapter(Date.class, new DateTypeDeserializer())
                .registerTypeAdapter(byte[].class, new ByteArrayTypeDeserializer())
                .create();
        }
        return gsonBuilder;
    }

    public static String getJsonArray(List<?> objectList) {
        if (objectList == null)
            return null;

        Gson gson = getGsonBuilder();
        JsonArray array = new JsonArray();
        for (Object obj : objectList) {
            JsonObject o = JsonParser.parseString(gson.toJson(obj)).getAsJsonObject();
//            String json = gson.toJson(obj);
            array.add(o);
        }

        return array.toString();
    }

//    public static String getJsonArrayFromUsers(List<User> userList) {
//        if (userList == null)
//            return null;
//
//        Gson gson = new GsonBuilder()
//                .excludeFieldsWithoutExposeAnnotation().create();
//
//        JsonArray array = new JsonArray();
//        for (User user : userList) {
//            String json = gson.toJson(user);
//            array.add(json);
//        }
//
//        return array.toString();
//    }

    public static Object getObjectWithExposeFields(Class clazz, String json) {
        if (json == null)
            return null;

        Gson gson = getGsonBuilder();
        return gson.fromJson(json, clazz);
    }

    public static Object getObject(Class clazz, String json) {
        if (json == null)
            return null;

        Gson gson = getGsonBuilder();
        return gson.fromJson(json, clazz);
    }

    public static List<Photo> getList(String json) {
        Type listType = new TypeToken<List<Photo>>(){}.getType();
        Gson gson = getGsonBuilder();
        return gson.fromJson(json, listType);
    }



    public static String getJson(Object o) {
        if (o == null)
            return null;

        Gson gson = getGsonBuilder();
        return gson.toJson(o);
    }
}
