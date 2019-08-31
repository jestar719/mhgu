package cn.jestar.convert.skill;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jestar.convert.Constants;
import cn.jestar.convert.bean.SkillBean;
import cn.jestar.convert.bean.SkillJewelryBean;

import static cn.jestar.convert.utils.ParserUtils.getAList;
import static cn.jestar.convert.utils.ParserUtils.getDoc;
import static cn.jestar.convert.utils.ParserUtils.parseAList;
import static cn.jestar.convert.utils.ParserUtils.writeDoc;


/**
 */
public class SkillParser {

    public Map<String, String> parseSkillName() throws IOException {
        File file = new File(Constants.DATA_PATH, "2200.html");
        Document doc = getDoc(file);
        Element a = doc.select("table.t1").first();
        Map map = parseAList(a);
        return map;
    }

    public SkillBean parseSkill(File file, SkillBean skillBean) throws IOException {
        Elements select = getDoc(file).select("table.t1");
        Elements tr = select.first().getElementsByTag("tr");
        int size = tr.size();
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

    public List<SkillJewelryBean> parseJewelry() throws IOException {
        List<SkillJewelryBean> jwdList = new ArrayList<>();
        SkillJewelryBean bean;
        for (int i = 2580; i <= 2582; i++) {
            File file = new File(Constants.DATA_PATH, String.format("%s.html", i));
            Document doc = getDoc(file);
            Elements elements = doc.select("table.t1");
            for (Element element : elements) {
                Elements a = element.select("a");
                Element first = a.first();
                bean = new SkillJewelryBean(first.text());
                bean.setUrl(first.attr("href"));
                bean.setSkill(a.get(1).text());
                bean.setDebuff(a.last().text());
                for (Element span : element.select("span")) {
                    String s = span.className();
                    if ("c_g b".equals(s)) {
                        bean.setSkillValue(bean.getInt(span.text()));
                    } else if ("c_r b".equals(s)) {
                        bean.setDebuffValue(bean.getInt(span.text()));
                        break;
                    }
                }
                jwdList.add(bean);
            }
        }
        return jwdList;
    }

    public void convertHtml(File file, Map<String, String> map) throws IOException {
        Document doc = getDoc(file);
        Element body = doc.body();
        convertInElements(map, getAList(body));
        writeDoc(file, doc);
    }



    public void convertSkillInType(File file, Map<String, String> map) throws IOException {
        Document doc = getDoc(file);
        Element body = doc.body();
        convertInElements(map, getAList(body));
        Elements span = body.getElementsByTag("span");
        convertInElements(map, span);
        writeDoc(file, doc);
    }

    public void convertSkillInDetail(File file, Map<String, String> map) throws IOException {
        Document doc = getDoc(file);
        Element body = doc.body();
        convertInElements(map, getAList(body));
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

    public void convertSkillInEquip(File file, Map<String, String> map) throws IOException {
        Document doc = getDoc(file);
        Element body = doc.body();
        convertInElements(map, getAList(body));
        convertInElements(map, body.select("table.t1 th"));
        convertInElements(map, body.select("table.t1 span.c_g"));
        writeDoc(file, doc);
    }

    public void convertSkillInJwerldy(File file, Map<String, String> map, Set<String> set) throws IOException {
        Document doc = getDoc(file);
        Element body = doc.body();
        convertInElements(map, getAList(body));
        Element h2 = body.selectFirst("h2");
        String text = h2.text();
        String trim = text.split("-")[0].trim();
        String s = map.get(trim);
        if (s != null) {
            h2.text(text.replace(trim, s));
        }
        replaceElement(map, body.getElementsByTag("h3").last());
        Elements t1 = body.getElementsByTag("table");
        Elements tag = t1.last().getElementsByTag("a");
        for (Element element : tag) {
            set.add(element.attr("href").replace("../", ""));
        }
        Element td = t1.first().selectFirst("td");
        String text1 = td.text().split(" ")[0].trim();
        String s1 = map.get(text1);
        if (s1 != null) {
            Elements span = td.getElementsByTag("span");
            td.text("");
            Element p = new Element("p");
            p.text(s1);
            td.appendChild(p);
            for (Element element : span) {
                p = new Element("p");
                p.text(element.text());
                td.appendChild(p);
            }
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
