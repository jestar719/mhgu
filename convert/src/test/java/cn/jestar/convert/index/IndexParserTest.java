package cn.jestar.convert.index;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import cn.jestar.convert.Constants;
import cn.jestar.convert.utils.JsonUtils;

import static cn.jestar.convert.utils.ParserUtils.getDoc;
import static cn.jestar.convert.utils.ParserUtils.writeDoc;

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

    @Test
    public void parseMonsterTest() throws IOException {
        File file = new File(Constants.TEMP_TRANSLATED_PATH, "monster.json");
        JsonUtils.writeJson(file, mParser.parseMonster());
    }

    @Test
    public void convertMonster() throws IOException {
        File file = new File(Constants.TEMP_TRANSLATED_PATH, "monster.json");
        HashMap<String, String> map = JsonUtils.fromString(new FileReader(file), HashMap.class);
        String url = "data/2501.html";
        mParser.convertMonsterName(url, map);
        file = new File(Constants.MH_PATH, url);
        Document doc = getDoc(file);
        for (Element e : doc.select("table.t1 a")) {
            String key = e.text().trim();
            String s = map.get(key);
            if (s != null) {
                e.text(s);
            }
        }
        writeDoc(file, doc);

    }
}