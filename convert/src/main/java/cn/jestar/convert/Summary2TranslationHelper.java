package cn.jestar.convert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import cn.jestar.convert.bean.LinkInfo;
import cn.jestar.convert.utils.JsonUtils;

/**
 * Created by 花京院 on 2019/2/2.
 */

public class Summary2TranslationHelper extends Copyable {
    private File mSummaryFile;
    private File mTraslationFile;

    public Summary2TranslationHelper() {
        mSummaryFile = new File(Constants.TEMP_SUMMARY_PATH);
        mTraslationFile = new File(Constants.TEMP_TRANSLATION_PATH);
    }


    public void fromStringMap(File file) throws IOException {
        String name = file.getName();
        File file2 = new File(mTraslationFile, name);
        if (!file2.exists()) {
            file2.mkdirs();
        }
        File[] files = file.listFiles();
        for (File source : files) {
            File target = new File(file2, source.getName());
            fromStringMap(source, target);
        }
    }


    public void fromStringMap(String name) throws FileNotFoundException {
        File source = new File(mSummaryFile, name);
        File target = new File(mTraslationFile, name);
        fromStringMap(source, target);
    }

    private void fromStringMap(File source, File target) throws FileNotFoundException {
        FileReader reader = new FileReader(source);
        Map<String, String> map = JsonUtils.fromString(reader, TreeMap.class);
        TreeSet<String> strings = new TreeSet<>(map.keySet());
        write(strings, target);
    }

    public void fromLinkInfo(File file) throws FileNotFoundException {
        String name = file.getName();
        File file2 = new File(mTraslationFile, name);
        if (!file2.exists()) {
            file2.mkdirs();
        }
        File[] files = file.listFiles();
        for (File source : files) {
            File target = new File(file2, source.getName());
            fromLinkInfo(source, target);
        }
    }

    public void fromLinkInfo(File source, File target) throws FileNotFoundException {
        FileReader reader = new FileReader(source);
        TreeSet<String> strings = fromLinkInfo(reader);
        write(strings, target);
    }

    private TreeSet<String> fromLinkInfo(FileReader reader) {
        LinkInfo info = JsonUtils.fromString(reader, LinkInfo.class);
        Set<String> strings1 = info.getData().keySet();
        return new TreeSet<>(strings1);
    }


    public void fromLinkInfo(String name) throws FileNotFoundException {
        File source = new File(mSummaryFile, name);
        File target = new File(mTraslationFile, name);
        fromLinkInfo(source, target);
    }
}
