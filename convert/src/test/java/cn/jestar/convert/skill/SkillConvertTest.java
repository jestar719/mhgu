package cn.jestar.convert.skill;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.jestar.convert.Constants;
import cn.jestar.convert.utils.JsonUtils;

/**
 * Created by 花京院 on 2019/2/20.
 */

public class SkillConvertTest {

    private SkillNameConvert mConvert;
    private String mSkill;

    @Before
    public void init() {
        mConvert = new SkillNameConvert("技能一览");
        mSkill = "skill";
    }

    @Test
    public void sortMap() throws IOException {
        Map<String, String> map = getMap();
        TreeMap<String, String> treeMap = new TreeMap<>(map);
        File file = new File(Constants.TEMP_TRANSLATED_PATH, mSkill);
        FileWriter writer = new FileWriter(new File(file, mConvert.mJsonFileName));
        writer.write(JsonUtils.toString(treeMap));
        writer.close();
    }

    @Test
    public void convertSkillNameInIndex() throws Exception {
        String temp = "data/%s.html";
        Map<String, String> map = getMap();
        List<String> list = mConvert.getList(map);
        for (int i = 2200; i < 2208; i++) {
            mConvert.translateFile(String.format(temp, i), map, list, null);
        }
        for (int i = 2580; i < 2583; i++) {
            mConvert.translateFile(String.format(temp, i), map, list, null);
        }
        for (int i = 2301; i < 2311; i++) {
            mConvert.translateFile(String.format(temp, i), map, list, null);
        }
        mConvert.translateFile(String.format(temp, 2847), map, list, null);
    }

    public Map<String, String> getMap() throws FileNotFoundException {
        File file = new File(Constants.TEMP_TRANSLATED_PATH, mSkill);
        return mConvert.getMap(mConvert.mJsonFileName, file);
    }

    @Test
    public void convertSkillNameInSkills() throws Exception {
        Map<String, String> map = getMap();
        List<String> list = mConvert.getList(map);
        File file1 = new File(Constants.TEMP_SUMMARY_PATH, mSkill);
        FileReader reader = new FileReader(new File(file1, mConvert.mJsonFileName));
        TreeMap<String, String> treeMap = JsonUtils.fromString(reader, TreeMap.class);
        for (String s : treeMap.values()) {
            mConvert.translateFile(s, map, list, null);
        }
    }

}
