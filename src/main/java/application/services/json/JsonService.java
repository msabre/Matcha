package application.services.json;

import application.services.json.typeAdapters.*;
import com.google.gson.*;

import com.google.gson.reflect.TypeToken;
import domain.entity.Photo;
import domain.entity.types.GenderType;
import domain.entity.types.SexualPreferenceType;

import java.lang.reflect.Type;
import java.util.List;

public class JsonService {

    private static Gson gsonExpose;
    private static Gson gson;

    private static Gson getGsonExpose() {
        if (gsonExpose == null) {
            gsonExpose = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(GenderType.class, new GenderTypeSerializer())
                .registerTypeAdapter(GenderType.class, new GenderTypeDeserializer())
                .registerTypeAdapter(SexualPreferenceType.class, new SexualPreferenceTypeSerializer())
                .registerTypeAdapter(SexualPreferenceType.class, new SexualPreferenceTypeDeserializer())
//                .registerTypeAdapter(Date.class, new DateTypeDeserializer())
//                .registerTypeAdapter(Date.class, new DateTypeSerializer())
                .registerTypeAdapter(byte[].class, new ByteArrayTypeDeserializer())
                .registerTypeAdapter(List.class, new CollectionAdapter())
                .setDateFormat("dd.MM.yyyy")
                .create();
        }
        return gsonExpose;
    }

    private static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(GenderType.class, new GenderTypeSerializer())
                    .registerTypeAdapter(GenderType.class, new GenderTypeDeserializer())
                    .registerTypeAdapter(SexualPreferenceType.class, new SexualPreferenceTypeSerializer())
                    .registerTypeAdapter(SexualPreferenceType.class, new SexualPreferenceTypeDeserializer())
//                    .registerTypeAdapter(Date.class, new DateTypeDeserializer())
                    .registerTypeAdapter(byte[].class, new ByteArrayTypeDeserializer())
                    .setDateFormat("dd.MM.yyyy")
                    .create();
        }
        return gson;
    }

    public static String getJsonArray(List<?> objectList) {
        if (objectList == null)
            return null;

        Gson gson = getGsonExpose();
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

    public static Object getObject(Class clazz, String json) {
        if (json == null)
            return null;

        Gson gson = getGson();
        return gson.fromJson(json, clazz);
    }

    public static Object getObjectByExposeFields(Class clazz, String json) {
        if (json == null)
            return null;

        Gson gson = getGsonExpose();
        return gson.fromJson(json, clazz);
    }

    public static List<Photo> getList(String json) {
        Type listType = new TypeToken<List<Photo>>(){}.getType();
        Gson gson = getGsonExpose();
        return gson.fromJson(json, listType);
    }

    public static String getJson(Object o) {
        if (o == null)
            return null;

        Gson gson = getGsonExpose();
        return gson.toJson(o);
    }

    public static String getParameter(String json, String paramName) {
        try {
            return JsonParser.parseString(json).getAsJsonObject().get(paramName).getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
