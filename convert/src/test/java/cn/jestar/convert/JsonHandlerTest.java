package cn.jestar.convert;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * 用与转换Summary中的数据到Traslation中
 * Created by 花京院 on 2019/2/2.
 */

public class JsonHandlerTest {

    private JsonHandler mHelper;

    @Before
    public void SetUp() {
        mHelper = new JsonHandler();
    }

    @Test
    public void sortTest() throws IOException {
        File skill = new File(Constants.TEMP_TRANSLATED_PATH, "skill");
        skill = new File(skill, "技能一览.json");
        new JsonHandler().sortJson(skill, false);
    }
}
