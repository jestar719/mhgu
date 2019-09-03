package cn.jestar.convert;

import com.google.gson.reflect.TypeToken;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.jestar.convert.utils.JsonUtils;

import static cn.jestar.convert.utils.ParserUtils.getDoc;
import static cn.jestar.convert.utils.ParserUtils.writeDoc;

/**
 * Created by 花京院 on 2019/9/2.
 */

public class CatRiceParser {
    private Map<String, String> mOther;
    private Map<String, CatRiceBean> mCatRiceMap;

    public CatRiceParser() {
        File file = new File(Constants.TEMP_TRANSLATED_PATH, "cat_rice.json");
        java.lang.reflect.Type type = new TypeToken<Map<String, Map<String, String>>>() {
        }.getType();
        try {
            Map<String, Map<String, String>> map = JsonUtils.fromStringByType(new FileReader(file), type);
            Map<String, String> names = map.get("name");
            Map<String, String> effect = map.get("effect");
            mOther = map.get("other");
            mCatRiceMap = new HashMap<>(map.size());
            for (String s : names.keySet()) {
                CatRiceBean bean = new CatRiceBean(names.get(s), s);
                bean.setEffect(effect.get(s));
                mCatRiceMap.put(s, bean);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getOther() {
        return mOther;
    }

    public Map<String, String> convertCatRiceEffect() throws IOException {
        File file = new File(Constants.DATA_PATH, "3001.html");
        Document doc = getDoc(file);
        convertTh(doc);
        Elements select = doc.select("table.t1 tr");
        Element first = select.first();
        Elements td = first.getElementsByTag("td");
        for (Element element : td) {
            convertOther(element);
        }
        int size = select.size();
        Map<String, String> map = new HashMap<>();
        for (int i = 1; i < size; i++) {
            Element element = select.get(i);
            Element a = element.selectFirst("a");
            String text = a.text().trim();
            CatRiceBean bean = mCatRiceMap.get(text);
            a.text(bean.getName());
            map.put(text, a.attr("href").replace("../", "").trim());
            element.selectFirst("td.left").text(bean.getEffect());
        }
        writeDoc(file, doc);
        return map;
    }

    public void convertCatRice() throws IOException {
        File file = new File(Constants.DATA_PATH, "2505.html");
        Document doc = getDoc(file);
        convertTh(doc);
        convertTextNodes(doc.selectFirst("div.box1"));
        Elements select = doc.select("table a");
        for (Element element : select) {
            String trim = element.text().trim();
            CatRiceBean catRiceBean = mCatRiceMap.get(trim);
            if (catRiceBean != null)
                element.text(catRiceBean.getName());
        }
        for (String s : mOther.keySet()) {
            String s1 = mOther.get(s);
            Elements tds = doc.select("td:contains(" + s + ")");
            if (!tds.isEmpty()) {
                for (Element td : tds) {
                    td.text(s1);
                }
            }
            Elements spans = doc.select("span:contains(" + s + ")");
            if (!spans.isEmpty()) {
                for (Element span : spans) {
                    span.text(s1);
                }
            }
        }
        writeDoc(file, doc);
    }

    private void convertTh(Document doc) {
        for (Element element : doc.select("table th")) {
            convertOther(element);
        }
    }

    public void convertRandomRice() throws IOException {
        File file = new File(Constants.DATA_PATH, "2960.html");
        Document doc = getDoc(file);
        Elements select = doc.select("table a");
        for (Element element : select) {
            String trim = element.text().trim();
            element.text(mCatRiceMap.get(trim).getName());
        }
        writeDoc(file, doc);
    }

    public void convertCatRiceSkill(File file, String name) throws IOException {
        CatRiceBean bean = mCatRiceMap.get(name);
        String cName = bean.getName();
        Document doc = getDoc(file);
        convertTextNodes(doc.selectFirst("div.box1"));
        Element div = doc.selectFirst("div.f_min");
        Element h3 = div.selectFirst("h3");
        String text = h3.text();
        h3.text(text.replace(name, cName));
        for (Element th : div.select("th")) {
            convertOther(th);
        }
        Elements t1 = div.select("table.t1");
        Element table = t1.first();
        Elements td = table.select("td");
        td.first().text(cName);
        td.last().text(bean.effect);
        table = t1.last();
        td = table.select("td:not(a)");
        for (Element element : td) {
            Elements span = element.select("span");
            element = span.size() == 0 ? element : span.first();
            convertOther(element);
        }
        for (Element a : table.select("a")) {
            String trim = a.text().trim();
            a.text(mCatRiceMap.get(trim).getName());
        }
        writeDoc(file, doc);
    }

    public void convertOther(Element element) {
        String trim = element.text().trim();
        String s = mOther.get(trim);
        if (s != null) {
            element.text(s);
        }
    }

    public void convertCatRiceDetail() throws IOException {
        File file = new File(Constants.DATA_PATH, "3001.html");
        Document doc = getDoc(file);
        File file1 = new File(Constants.TEMP_TRANSLATED_PATH, "food.json");
        java.lang.reflect.Type type = new TypeToken<Map<String, Map<String, String>>>() {
        }.getType();
        Map<String, Map<String, String>> map = JsonUtils.fromStringByType(new FileReader(file1), type);
        Map<String, String> shicai = map.get("rice");
        Elements select = doc.select("tbody td[rowspan=2]");
        for (Element e : select.select("span.b")) {
            String trim = e.text().trim();
            String s = shicai.get(trim);
            if (s != null) {
                e.text(s);
            }
        }
        writeDoc(file, doc);
    }


    public void convertTextNodes(Element e) {
        for (TextNode element : e.textNodes()) {
            if (!element.isBlank()) {
                String text = element.text().trim();
                String s = mOther.get(text);
                if (s != null) {
                    element.text(s);
                }
            }
        }
    }


    class CatRiceBean {
        private String name;
        private String jName;
        private String effect;

        public CatRiceBean(String name, String jName) {
            this.name = name;
            this.jName = jName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getjName() {
            return jName;
        }

        public void setjName(String jName) {
            this.jName = jName;
        }

        public String getEffect() {
            return effect;
        }

        public void setEffect(String effect) {
            this.effect = effect;
        }
    }
}
