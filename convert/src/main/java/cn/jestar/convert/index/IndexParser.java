package cn.jestar.convert.index;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import cn.jestar.convert.Constants;

import static cn.jestar.convert.utils.ParserUtils.getDoc;
import static cn.jestar.convert.utils.ParserUtils.parseA;
import static cn.jestar.convert.utils.ParserUtils.parseAList;

/**
 * 索引解析
 * Created by 花京院 on 2019/8/31.
 */

public class IndexParser {

    public Map<String, Map<String, String>> parserSkill() throws IOException {
        File file = new File(Constants.DATA_PATH, "2200.html");
        Map<String, Map<String, String>> index = new HashMap<>();
        Document doc = getDoc(file);
        Element element = doc.selectFirst("div#navi1");
        index.put("skill_type", parseAList(element));
        index.put("skill", parseAList(doc.selectFirst("table.t1")));
        doc = getDoc(new File(Constants.INDEX));
        element = doc.select("table.t0").get(3);
        Map<String, String> value = parseAList(element);
        index.put("jwd_index", value);
        Collection<String> values = value.values();
        value = new TreeMap<>();
        index.put("jwd", value);
        for (String s : values) {
            doc = getDoc(new File(Constants.MH_PATH, s));
            for (Element element1 : doc.select("table.t1")) {
                Element a = element1.selectFirst("a");
                parseA(a, value);
            }
        }
        return index;
    }
}
