package cn.jestar.convert.skill;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
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
    }

    @Test
    public void convertSkillNameTest() throws Exception {
        mSkill = "skill";
        String temp = "data/%s.html";
        File file = new File(Constants.TEMP_TRANSLATED_PATH, mSkill);
        Map<String, String> map = mConvert.getMap(mConvert.mJsonFileName, file);
        List<String> list = mConvert.getList(map);
        for (int i = 2200; i < 2208; i++) {
            mConvert.translateFile(String.format(temp, i), map, list, null);
        }
        for (int i = 2580; i < 2583; i++) {
            mConvert.translateFile(String.format(temp, i), map, list, null);
        }
    }

    @Test
    public void convertSkillsName() throws Exception {
        mSkill = "skill";
        File file = new File(Constants.TEMP_TRANSLATED_PATH, mSkill);
        Map<String, String> map = mConvert.getMap(mConvert.mJsonFileName, file);
        List<String> list = mConvert.getList(map);
        File file1 = new File(Constants.TEMP_SUMMARY_PATH, mSkill);
        FileReader reader = new FileReader(new File(file1, mConvert.mJsonFileName));
        TreeMap<String, String> treeMap = JsonUtils.fromString(reader, TreeMap.class);
        for (String s : treeMap.values()) {
            mConvert.translateFile(s, map, list, null);
        }
    }

}
