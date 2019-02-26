package cn.jestar.convert.skill;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import cn.jestar.convert.Constants;
import cn.jestar.convert.index.UrlCompiler;
import cn.jestar.convert.utils.JsonUtils;

/**
 * 技能珠解析
 * Created by 花京院 on 2019/2/20.
 */

public class SkillJewerCompiler extends UrlCompiler {

    private TreeMap<String, String> mMap;

    public SkillJewerCompiler() {
        mMap = new TreeMap<>();
    }

    @Override
    protected boolean onRead(BufferedReader reader, String text) {
        if (text.contains("【")) {
            setMap(text, mMap);
        }
        return true;
    }

    public Map<String, String> getMap() {
        readFile();
        return mMap;
    }

    public void writeMap() {
        writeMap(mMap);
    }

    public void writeMap(Map<String, String> map) {
        File skill = new File(Constants.TEMP_SUMMARY_PATH, "skill");
        try (FileWriter writer = new FileWriter(new File(skill, "技能珠.json"));
             FileWriter writer1 = new FileWriter(new File(skill, "技能珠list.json"))
        ) {
            writer.write(JsonUtils.toString(map));
            writer.close();
            List<Set<String>> list = new ArrayList<>();
            TreeSet<String> set = new TreeSet<>(map.values());
            list.add(set);
            set = new TreeSet<>();
            for (String s : map.keySet()) {
                set.add(s.substring(0, s.indexOf("【")));
            }
            list.add(set);
            writer1.write(JsonUtils.toString(list));
            writer1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
