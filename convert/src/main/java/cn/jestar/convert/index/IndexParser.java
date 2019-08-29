package cn.jestar.convert.index;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by 花京院 on 2019/8/29.
 */

public class IndexParser {
    public static Document getDoc(File file) throws IOException {
        return Jsoup.parse(file, "utf-8");
    }

    public static Map parseAlist(Elements elements) {
        HashMap<String, String> map = new LinkedHashMap<>();
        for (Element a : elements) {
            map.put(a.text(), a.attr("href").replace("../", ""));
        }
        return map;
    }

    public static Map parseAlist(Element element) {
        Elements elements = getA(element);
        return parseAlist(elements);
    }

    public static Elements getA(Element element) {
        return element.getElementsByTag("a");
    }
}
