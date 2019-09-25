package cn.jestar.convert.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by 花京院 on 2019/8/29.
 */

public class ParserUtils {
    public static Document getDoc(File file) throws IOException {
        return Jsoup.parse(file, "utf-8");
    }

    public static void writeDoc(File file, Document doc) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write(doc.outerHtml());
        writer.close();
    }

    public static Map<String, String> parseAList(Elements elements) {
        HashMap<String, String> map = new LinkedHashMap<>();
        for (Element a : elements) {
            map.put(a.text(), a.attr("href").replace("../", ""));
        }
        return map;
    }

    public static Map<String, String> parseA(Element a, Map<String, String> map) {
        map.put(a.text(), a.attr("href").replace("../", ""));
        return map;
    }


    public static Map<String, String> parseAList(Element element) {
        Elements elements = getAList(element);
        return parseAList(elements);
    }

    public static void matchCovert(Element element,Map<String,String> map){
        String trim = element.text().trim();
        String s = map.get(trim);
        if (s!=null){
            element.text(s);
            System.out.println(trim+" "+s);
        }
    }

    public static void matchCovert(Elements elements,Map<String,String> map){
        for (Element element : elements) {
            matchCovert(element,map);
        }
    }

    public static Elements getAList(Element element) {
        return element.getElementsByTag("a");
    }
}
