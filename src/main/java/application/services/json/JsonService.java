package application.services.json;

import application.services.json.typeAdapters.*;
import com.google.gson.*;

import com.google.gson.reflect.TypeToken;
import domain.entity.Photo;
import domain.entity.model.types.GenderType;
import domain.entity.model.types.SexualPreferenceType;

import java.lang.reflect.Array;
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

    public static String getJsonArrayWithExpose(List<?> objectList) {
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

    public static String getJsonArray(List<?> objectList) {
        if (objectList == null)
            return null;

        Gson gson = getGson();
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

    public static List<Photo> getPhotoList(String json) {
        Type listType = new TypeToken<List<Photo>>(){}.getType();
        Gson gson = getGson();
        return gson.fromJson(json, listType);
    }

    public static String getJsonWithExposeFields(Object o) {
        if (o == null)
            return null;

        Gson gson = getGsonExpose();
        return gson.toJson(o);
    }

    public static String getJsonChat(Object o) {
        if (o == null)
            return null;

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd' 'HH:mm:ss").create();
        return gson.toJson(o);
    }

    // Парсит из json'а массив по имени поля
    public static Object[] parseArray(String source, String field, Class clazz) {
        if (!clazz.isArray())
            return new Object[0];

        JsonObject jsonObject = getGson().fromJson(source, JsonObject.class);
        JsonArray jsonArray = jsonObject.getAsJsonArray(field);

        return (Object[]) getGson().fromJson(jsonArray, clazz);
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
