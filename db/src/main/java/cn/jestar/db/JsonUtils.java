package cn.jestar.db;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 花京院 on 2019/1/27.
 */

public class JsonUtils {
    private final static Gson sGson = new Gson();

    public static String toString(Object o) {
        return sGson.toJson(o);
    }

    public static <T> T getObject(String string, Class<T> cls) {
        return sGson.fromJson(string, cls);
    }

    public static <T> T getObject(Reader reader, Class<T> cls) throws IOException {
        T t = sGson.fromJson(reader, cls);
        reader.close();
        return t;
    }

    public static <T> List<T> getList(String string, Class<T> clazz) {
        List<T> lst = new ArrayList<>();
        try {
            JsonArray array = new JsonParser().parse(string).getAsJsonArray();
            for (final JsonElement elem : array) {
                lst.add(sGson.fromJson(elem, clazz));
            }
        } catch (Exception e) {
        }
        return lst;
    }

    public static <T> List<T> getList(Reader reader, Class<T> clazz) {
        List<T> lst = new ArrayList<>();
        try {
            JsonArray array = new JsonParser().parse(reader).getAsJsonArray();
            for (final JsonElement elem : array) {
                lst.add(sGson.fromJson(elem, clazz));
            }
            reader.close();
        } catch (Exception e) {
        }
        return lst;
    }
}
