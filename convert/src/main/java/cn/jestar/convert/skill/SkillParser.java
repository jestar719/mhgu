package cn.jestar.convert.skill;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import cn.jestar.convert.Constants;
import cn.jestar.convert.bean.SkillBean;
import cn.jestar.convert.index.IndexParser;

import static cn.jestar.convert.index.IndexParser.getA;
import static cn.jestar.convert.index.IndexParser.getDoc;

/**
 * 技能的解析
 * Created by 花京院 on 2019/8/29.
 */

public class SkillParser {

    public Map<String, String> parseSkillName() throws IOException {
        File file = new File(Constants.DATA_PATH, "2200.html");
        Document doc = getDoc(file);
        Element a = doc.select("table.t1").first();
        Map map = IndexParser.parseAlist(a);
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

    public void convertHtml(File file, Map<String, String> map) throws IOException {
        Document doc = getDoc(file);
        Element body = doc.body();
        convertInA(map, getA(body));
        FileWriter writer = new FileWriter(file);
        writer.write(doc.outerHtml());
        writer.close();
    }

    public void convertSkillInType(File file, Map<String, String> map) throws IOException {
        Document doc = getDoc(file);
        Element body = doc.body();
        convertInA(map, getA(body));
        Elements span = body.getElementsByTag("span");
        convertInA(map, span);
        FileWriter writer = new FileWriter(file);
        writer.write(doc.outerHtml());
        writer.close();
    }

    private void convertInA(Map<String, String> map, Elements elements) {
        for (Element element : elements) {
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
}
