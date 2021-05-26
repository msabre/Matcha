package application.services.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import domain.entity.User;

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

        Gson gson = new Gson();

        return gson.fromJson(json, clazz);
    }

    public static String getJson(Object o) {
        if (o == null)
            return null;

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation().create();

        return gson.toJson(o);
    }
}
