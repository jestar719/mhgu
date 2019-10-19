package cn.jestar.convert.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
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

    public static <T> T fromString(String string, Class<T> cls) {
        return sGson.fromJson(string, cls);
    }

    public static <T> T fromString(Reader reader, Class<T> cls) {
        return sGson.fromJson(reader, cls);
    }

    public static <T> List<T> toList(Reader string, Class<T> clazz) {
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


    public static <T> T fromStringByType(Reader reader, Type type) {
        return sGson.fromJson(reader, type);
    }

    public static void writeJson(File file, Object obj) throws IOException {
        String s = sGson.toJson(obj);
        FileWriter writer = new FileWriter(file);
        writer.write(s);
        writer.close();
    }
}
