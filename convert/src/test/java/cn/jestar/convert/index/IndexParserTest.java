package cn.jestar.convert.index;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import cn.jestar.convert.Constants;
import cn.jestar.convert.utils.JsonUtils;

/**
 * Created by 花京院 on 2019/8/31.
 */
public class IndexParserTest {

    private IndexParser mParser;

    @Before
    public void setUp() throws Exception {
        mParser = new IndexParser();
    }

    @Test
    public void parseSkillIndexTest() throws IOException {
        File file = new File(Constants.TEMP_SUMMARY_PATH, "skill_index.json");
        JsonUtils.writeJson(file, mParser.parserSkill());
    }
}