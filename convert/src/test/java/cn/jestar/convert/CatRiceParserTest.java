package cn.jestar.convert;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.util.Map;

import cn.jestar.convert.utils.JsonUtils;

import static cn.jestar.convert.utils.ParserUtils.getDoc;
import static cn.jestar.convert.utils.ParserUtils.writeDoc;

/**
 * Created by 花京院 on 2019/9/2.
 */
public class CatRiceParserTest {

    private CatRiceParser mParser;

    @Before
    public void setUp() throws Exception {
        mParser = new CatRiceParser();
    }

    @Test
    public void convertCatRiceEffect() throws Exception {
        Map<String, String> map = mParser.convertCatRiceEffect();
    }

    @Test
    public void convertRandomRice() throws Exception {
        mParser.convertRandomRice();
    }

    @Test
    public void convertCatRice() throws Exception {
        mParser.convertCatRice();
    }

    @Test
    public void convertCatRiceSkill() throws Exception {
        File file = new File(Constants.TEMP_SUMMARY_PATH, "cat_rice_index.json");
        Map<String, String> map = JsonUtils.fromString(new FileReader(file), Map.class);
        for (String s : map.values()) {
            File page = new File(Constants.MH_PATH, s);
            Document doc = getDoc(page);
            Elements a = doc.select("a");
            for (Element element : a) {
                mParser.convertOther(element);
            }
            writeDoc(page, doc);
        }
    }

    @Test
    public void convertCatRiceDetail() throws Exception {
        mParser.convertCatRiceDetail();
    }
}