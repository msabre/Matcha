package application.services.json;

import application.services.json.typeAdapters.*;
import com.google.gson.*;

import domain.entity.User;
import domain.entity.model.types.GenderType;
import domain.entity.model.types.SexualPreferenceType;

import java.util.Date;
import java.util.List;

public class JsonService {

    public static String getJsonArray(List<?> objectList) {
        if (objectList == null)
            return null;

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation().create();

        JsonArray array = new JsonArray();
        for (Object obj : objectList) {
            String json = gson.toJson(obj);
            array.add(json);
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

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation().create();

        return gson.fromJson(json, clazz);
    }

    public static Object getObject(Class clazz, String json) {
        if (json == null)
            return null;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(GenderType.class, new GenderTypeDeserializer());
        gsonBuilder.registerTypeAdapter(SexualPreferenceType.class, new SexualPreferenceTypeDeserializer());
        gsonBuilder.registerTypeAdapter(Date.class, new DateTypeDeserializer());

        Gson gson = gsonBuilder.create();

        return gson.fromJson(json, clazz);
    }

    public static String getJson(Object o) {
        if (o == null)
            return null;


        GsonBuilder gsonBuilder = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(GenderType.class, new GenderTypeSerializer())
                .registerTypeAdapter(SexualPreferenceType.class, new SexualPreferenceTypeSerializer());

        Gson gson = gsonBuilder.create();

        return gson.toJson(o);
    }
}
