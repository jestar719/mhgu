package cn.jestar.convert.skill;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import cn.jestar.convert.Constants;

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
        File file = new File(Constants.TEMP_TRANSLATED_PATH, mSkill);
        Map<String, String> map = mConvert.getMap(mConvert.mJsonFileName, file);
        List<String> list = mConvert.getList(map);
        mConvert.translateFile("data/2200.html", map, list, null);
    }


}
