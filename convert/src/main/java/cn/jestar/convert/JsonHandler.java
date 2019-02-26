package cn.jestar.convert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import cn.jestar.convert.utils.JsonUtils;

/**
 * Created by 花京院 on 2019/2/2.
 */

public class JsonHandler extends Copyable {
    private final File mTranslatedFile;
    private File mSummaryFile;
    private File mTranslationFile;

    public JsonHandler() {
        mSummaryFile = new File(Constants.TEMP_SUMMARY_PATH);
        mTranslationFile = new File(Constants.TEMP_TRANSLATION_PATH);
        mTranslatedFile = new File(Constants.TEMP_TRANSLATED_PATH);
    }


    public void copyListFromMap(File source, File targetDir, File json, boolean isKey) throws IOException {
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        TreeMap<String, String> map = JsonUtils.fromString(new FileReader(json), TreeMap.class);
        String s = JsonUtils.toString(isKey ? map.keySet() : map.values());
        for (File file : source.listFiles()) {
            String name = file.getName();
            File file1 = new File(targetDir, name);
            write(file1, s);
        }
    }


    public void sortJson(File file, boolean isList) throws IOException {
        FileWriter writer = new FileWriter(file);
        String str = isList ? sortList(file) : sortMap(file);
        writer.write(str);
        writer.close();
    }

    private String sortMap(File file) throws FileNotFoundException {
        return JsonUtils.toString(getMap(file));
    }

    private Map<String, String> getMap(File file) throws FileNotFoundException {
        TreeMap<String, String> map = JsonUtils.fromString(new FileReader(file), TreeMap.class);
        return map;
    }

    public String sortList(File file) throws FileNotFoundException {
        List<String> list = getList(file);
        TreeSet<String> strings = new TreeSet<>(list);
        return JsonUtils.toString(strings);
    }

    public List<String> getList(File file) throws FileNotFoundException {
        return JsonUtils.toList(new FileReader(file), String.class);
    }

}
