package cn.jestar.convert.skill;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import cn.jestar.convert.Constants;
import cn.jestar.convert.bean.SkillBean;

import static cn.jestar.convert.index.IndexParser.getA;
import static cn.jestar.convert.index.IndexParser.getDoc;
import static cn.jestar.convert.index.IndexParser.parseAlist;


/**
 */
public class SkillParser {

    public Map<String, String> parseSkillName() throws IOException {
        File file = new File(Constants.DATA_PATH, "2200.html");
        Document doc = getDoc(file);
        Element a = doc.select("table.t1").first();
        Map map = parseAlist(a);
        return map;
    }

    public SkillBean parseSkill(File file) throws IOException {
        Elements select = getDoc(file).select("table.t1");
        Elements tr = select.first().getElementsByTag("tr");
        int size = tr.size();
        SkillBean skillBean = new SkillBean();
        skillBean.setUrl(file.getName());
        for (int i = 1; i < size; i++) {
            SkillBean.SkillEffect effect = new SkillBean.SkillEffect();
            Element element = tr.get(i);
            Elements elements = element.select("td.b");
            if (i == 1) {
                skillBean.setName(elements.first().text().replace("[XX]", "").trim());
                effect.setName(elements.get(1).text());
            } else {
                effect.setName(elements.first().text());
            }
            effect.setValue(Integer.parseInt(elements.last().text()));
            effect.setDescription(element.getElementsByTag("td").last().text());
            skillBean.addSkillEffect(effect);
        }
        Elements tds = select.last().getElementsByTag("tr").last().getElementsByTag("td");
        size = tds.size();
        int[] max = new int[size - 1];
        for (int i = 1; i < size; i++) {
            int value = 0;
            try {
                value = Integer.parseInt(tds.get(i).text());
            } catch (NumberFormatException e) {
            }
            max[i - 1] = value;
        }
        skillBean.setMaxValues(max);
        return skillBean;
    }

    public Map<String, String> parseJwerd() throws IOException {
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 2580; i <= 2582; i++) {
            File file = new File(Constants.DATA_PATH, String.format("%s.html", i));
            Document doc = getDoc(file);
            Elements elements = doc.select("td[rowspan=2] a");
            convertInElements(map, elements);
        }
        return map;
    }

    public void convertHtml(File file, Map<String, String> map) throws IOException {
        Document doc = getDoc(file);
        Element body = doc.body();
        convertInElements(map, getA(body));
        writeDoc(file, doc);
    }

    public void writeDoc(File file, Document doc) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write(doc.outerHtml());
        writer.close();
    }

    public void convertSkillInType(File file, Map<String, String> map) throws IOException {
        Document doc = getDoc(file);
        Element body = doc.body();
        convertInElements(map, getA(body));
        Elements span = body.getElementsByTag("span");
        convertInElements(map, span);
        writeDoc(file, doc);
    }

    public void convertSkillInDetail(File file, Map<String, String> map) throws IOException {
        Document doc = getDoc(file);
        Element body = doc.body();
        convertInElements(map, getA(body));
        Elements select = body.select("table.t1");
        convertInElements(map, select.first().select("td.b"));
        replaceElement(map, select.last().selectFirst("td"));
        convertInElements(map, doc.select("th"));
        convertInElements(map, doc.select("h4"));
        Element td = doc.select("div.box1").last();
        String[] split = map.get(td.text().trim()).split("<br>");
        td.text("");
        for (String s : split) {
            td.append("<p>" + s + "</p>");
        }
        writeDoc(file, doc);
    }

    public void convertInElements(Map<String, String> map, Elements elements) {
        for (Element element : elements) {
            replaceElement(map, element);
        }
    }

    public void replaceElement(Map<String, String> map, Element element) {
        String text = element.text().trim();
        int index = text.indexOf("+");
        if (index > 0 || (index = text.indexOf("-")) > 0) {
            String substring = text.substring(0, index);
            String s = map.get(substring);
            if (s != null) {
                element.text(text.replace(substring, s));
            }
        } else {
            String s = map.get(text);
            if (s != null) {
                element.text(s);
            }
        }
    }
}
