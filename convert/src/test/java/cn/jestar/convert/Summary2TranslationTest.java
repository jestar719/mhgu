package cn.jestar.convert;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 用与转换Summary中的数据到Traslation中
 * Created by 花京院 on 2019/2/2.
 */

public class Summary2TranslationTest {

    private Summary2TranslationHelper mHelper;

    @Before
    public void SetUp() {
        mHelper = new Summary2TranslationHelper();
    }

    @Test
    public void createSkillList() throws FileNotFoundException {
        String name = "技能一览.json";
        mHelper.fromStringMap(name);
    }

    @Test
    public void createCatRiceSkillList() throws FileNotFoundException {
        String name = "猫饭技能.json";
        mHelper.fromLinkInfo(name);
    }

    @Test
    public void createWeaponNameList() throws IOException {
        mHelper.fromStringMap(new File(Constants.TEMP_SUMMARY_PATH, "weapon"));
    }


}
