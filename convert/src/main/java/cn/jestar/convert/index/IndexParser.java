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
import static cn.jestar.convert.utils.ParserUtils.writeDoc;

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

    public Map<String, String> parseMonster() throws IOException {
        File file = new File(Constants.DATA_PATH, "2974.html");
        Document doc = getDoc(file);
        Map<String, String> map = new HashMap<>();
        for (Element e : doc.select("div[id=navi1] a")) {
            String s = e.text().trim();
            map.put(s, s);
        }
        return map;
    }

    public void convertMonsterName(String url, Map<String, String> map) throws IOException {
        File file = new File(Constants.MH_PATH, url);
        Document doc = getDoc(file);
        for (Element e : doc.select("div[id=navi1] a")) {
            String s = e.text().trim();
            String s1 = map.get(s);
            if (s1 != null) {
                e.text(s1);
            }
        }
        Element a = doc.select("ul[id=bread] a").last();
        String text = a.text().trim();
        String s = map.get(text);
        if (s != null) {
            a.text(s);
            Element div = doc.selectFirst("div.f_min");
            Element h3 = div.selectFirst("h3");
            String string = h3.text();
            h3.text(string.replace(text, s));
            Element td = div.select("table.t1 td.b").first();
            string = td.text();
            td.text(string.replace(text, s));
        }
        writeDoc(file, doc);
    }
}
