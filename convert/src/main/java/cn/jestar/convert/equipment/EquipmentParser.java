package cn.jestar.convert.equipment;

import com.google.gson.reflect.TypeToken;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import cn.jestar.convert.Constants;
import cn.jestar.convert.bean.BaseEquip;
import cn.jestar.convert.bean.EquipIndexBean;
import cn.jestar.convert.bean.FullEquip;
import cn.jestar.convert.bean.LinkInfo;
import cn.jestar.convert.utils.JsonUtils;

import static cn.jestar.convert.bean.BaseEquip.TYPE.ALL;
import static cn.jestar.convert.bean.BaseEquip.TYPE.ARCHER;
import static cn.jestar.convert.utils.ParserUtils.getAList;
import static cn.jestar.convert.utils.ParserUtils.getDoc;
import static cn.jestar.convert.utils.ParserUtils.matchCovert;
import static cn.jestar.convert.utils.ParserUtils.writeDoc;

/**
 * 装备索引为装备一览，防具稀有度1-X，装备详情页，技能页，材料页
 * 需要翻译的文本为装备系列名，及装备详细名。
 * 含系列名的为装备一览，防具稀有度1-X，材料页
 * 含装备名的为防具稀有度1-X，装备详情页，技能页，材料页
 * Created by 花京院 on 2019/9/4.
 */

public class EquipmentParser {
    public static final String CATALOG_NAME = "equip/equip_catalog.json";
    public static final String EQUIP_DETAIL = "equip/equip_detail.json";
    public static final String CATALOG = "catalog";
    public static final String CATALOG_PAGE_NAME = "equip/%s.json";
    public static final String EQUIP_INDEX_NAME = "equip_index.json";


    /**
     * 从装备一览中解析装备页
     *
     * @throws IOException
     */
    public void parseEquipCatalog() throws IOException {
        File file = new File(Constants.TEMP_SUMMARY_PATH, EQUIP_INDEX_NAME);
        Type type = new TypeToken<List<LinkInfo>>() {
        }.getType();
        List<LinkInfo> map = JsonUtils.fromStringByType(new FileReader(file), type);
        Map<String, String> map1 = map.get(1).getData();
        Map<String, String> tree = new TreeMap<>();
        for (String s : map1.keySet()) {
            tree.put(s, s);
        }
        JsonUtils.writeJson(new File(Constants.TEMP_TRANSLATED_PATH, CATALOG_NAME), tree);
    }

    public List<List<FullEquip>> paserEquipDetail(String page) throws IOException {
        List<List<FullEquip>> equips = new ArrayList<>();
        File file = new File(Constants.MH_PATH, page);
        Document doc = getDoc(file);
        for (Element e : doc.select("div[class^=panel]:has(table)")) {
            String name = e.className();
            int sex = BaseEquip.SEX.ALL;
            if (name.endsWith("info2")) {
                sex = BaseEquip.SEX.MAN;
            } else if (name.endsWith("danger")) {
                sex = BaseEquip.SEX.FELMAN;
            }
            String url = e.selectFirst("a").attr("href").replace("../", "").trim();
            doc = getDoc(new File(Constants.MH_PATH, url));
            Elements select = doc.select("table.t1");
            String td = select.first().selectFirst("td").text().trim();
            int rare = 11;
            try {
                rare = Integer.parseInt(td);
            } catch (NumberFormatException e1) {

            }
            List<FullEquip> list = createEquips(select.get(1), select.get(2), sex, rare, url);
            equips.add(list);
        }
        return equips;
    }

    private List<FullEquip> createEquips(Element values, Element skills, int sex, int rare, String url) {
        int index = 0;
        List<FullEquip> equips = new ArrayList<>();
        Elements trs = values.select("tr:has(td)");
        Elements skillTrs = skills.select("tr:has(td)");
        int size = values.select("tr[style^=background]").size();
        int type = size > 1 ? BaseEquip.TYPE.FIGHT : ALL;
        int num = trs.size();
        for (int i = 0; i < num; i++) {
            Element tr = trs.get(i);
            String style = tr.attr("style");
            if (style != null && style.contains("background")) {
                System.out.println(style);
                if (type != ALL) {
                    index += 5;
                    type = ARCHER;
                }
            } else {
                FullEquip equip = new FullEquip();
                equip.setUrl(url);
                equip.setRare(rare);
                equip.setSex(sex);
                equip.setType(type);
                equips.add(equip);
                Element td = tr.selectFirst("td.left");
                equip.setName(td.text().trim());
                String src = td.selectFirst("img").attr("src");
                int part = src.lastIndexOf("/");
                part = Integer.parseInt(src.substring(part + 2, part + 3)) - 1;
                equip.setPart(part);
                equip.setId(index + part);
                td = td.nextElementSibling();
                equip.setDefence(getIntValue(td));
                td = td.nextElementSibling();
                equip.setMaxDefence(getIntValue(td));
                td = td.nextElementSibling();
                equip.setFire(getIntValue(td.selectFirst("span[class^=c_]")));
                td = td.nextElementSibling();
                equip.setWater(getIntValue(td.selectFirst("span[class^=c_]")));
                td = td.nextElementSibling();
                equip.setFlash(getIntValue(td.selectFirst("span[class^=c_]")));
                td = td.nextElementSibling();
                equip.setIce(getIntValue(td.selectFirst("span[class^=c_]")));
                td = td.nextElementSibling();
                equip.setDragon(getIntValue(td.selectFirst("span[class^=c_]")));
                Element e = skillTrs.get(i);
                Elements tds = e.select("td");
                equip.setSkills(getSkills(tds.last()));
                String slot = tds.get(1).text().trim();
                int slotNum;
                int start = slot.indexOf("-");
                int end = slot.lastIndexOf("-");
                if (start == end) {
                    slotNum = start < 0 ? 0 : 1;
                } else {
                    slotNum = end - start + 1;
                }
                equip.setSlotNum(3 - slotNum);
            }
        }
        return equips;
    }

    private List<BaseEquip.EquipSkill> getSkills(Element e) {
        Elements a = e.select("a");
        Elements span = e.select("span");
        int size = a.size();
        ArrayList<BaseEquip.EquipSkill> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            BaseEquip.EquipSkill skill = new BaseEquip.EquipSkill();
            skill.setId(i);
            skill.setName(a.get(i).text().trim());
            skill.setValue(getIntValue(span.get(i)));
            list.add(skill);
        }
        return list;
    }

    public int getIntValue(Element e) {
        return Integer.parseInt(e.text().trim());
    }

    public void parseEquipNameInPage() throws IOException {
        File file = new File(Constants.TEMP_SUMMARY_PATH, EQUIP_INDEX_NAME);
        List<LinkInfo> list = JsonUtils.toList(new FileReader(file), LinkInfo.class);
        Map<String, String> data = list.get(0).getData();
        Map<String, List<LinkInfo>> map = new LinkedHashMap<>();
        for (Map.Entry<String, String> key : data.entrySet()) {
            List<EquipIndexBean> beans = getEquipListIndex(key.getValue());
            List<LinkInfo> infos = new ArrayList<>(beans.size());
            for (EquipIndexBean bean : beans) {
                LinkInfo info = new LinkInfo(bean.getName());
                LinkedHashMap<String, String> map1 = new LinkedHashMap<>();
                for (String s : bean.getEquipNameList()) {
                    map1.put(s, s);
                }
                info.setData(map1);
                infos.add(info);
            }
            String name = key.getKey();
            map.put(name, infos);
        }
        JsonUtils.writeJson(new File(Constants.TEMP_TRANSLATED_PATH, EQUIP_DETAIL), map);
    }

    private List<EquipIndexBean> getEquipListIndex(String page) throws IOException {
        File file = new File(Constants.MH_PATH, page);
        Document doc = getDoc(file);
        Elements elements = doc.select("div[class^=panel]:has(table)");
        List<EquipIndexBean> equips = new ArrayList<>(elements.size());
        for (Element element : elements) {
            EquipIndexBean bean = new EquipIndexBean();
            equips.add(bean);
            Element a = element.selectFirst("a");
            String name = a.text().trim();
            bean.setName(name);
            String url = a.attr("href").trim().replace("../", "");
            bean.setUrl(url);
            Elements select = element.select("tr[class^=view_panel_body]");
            Set<String> set = new LinkedHashSet<>();
            for (Element e : select) {
                set.add(e.selectFirst("td.left a").text().trim());
            }
            bean.setEquipNameList(new ArrayList<>(set));
        }
        return equips;
    }


    public void convertTitle() throws IOException {
        File file = new File(Constants.TEMP_SUMMARY_PATH, EQUIP_INDEX_NAME);
        Type type = new TypeToken<List<LinkInfo>>() {
        }.getType();
        List<LinkInfo> list = JsonUtils.fromStringByType(new FileReader(file), type);
        file = new File(Constants.TEMP_TRANSLATED_PATH, CATALOG_NAME);
        type = new TypeToken<Map<String, Map<String, String>>>() {
        }.getType();
        Map<String, Map<String, String>> map = JsonUtils.fromStringByType(new FileReader(file), type);
        Map<String, String> convertText = map.get(CATALOG);
        Map<String, String> other = map.get("other");
        Map<String, String> index = list.get(3).getData();
        Set<String> urls = new HashSet<>();
        for (String url : index.values()) {
            convertCatalogInDetail(convertText, other, url, urls);
        }
        String title = other.get("title");
        for (String url : urls) {
            convertCatalogInMatierials(url, convertText, title);
        }
    }

    private void convertCatalogInMatierials(String url, Map<String, String> convertText, String title) throws IOException {
        File file = new File(Constants.MH_PATH, url);
        Document doc = getDoc(file);
        for (Element a : doc.select("table td").last().select("a")) {
            String text = a.text().trim();
            String key = text.contains(title) ? text.split(title)[0] : text;
            String value = convertText.get(key);
            if (value != null) {
                a.text(text.replace(key, value));
            }
        }
        writeDoc(file, doc);
    }

    private void convertCatalogInDetail(Map<String, String> convertText, Map<String, String> other, String url, Set<String> urls) throws IOException {
        File file;
        file = new File(Constants.MH_PATH, url);
        Document doc = getDoc(file);
        Element e = doc.select("div#navi1 span").get(2);
        matchCovert(e, other);
        Element last = doc.select("div.box1 a").last();
        convertE(other, last);
        last = doc.selectFirst("div.f_min");
        for (Element element : last.select("h3")) {
            convertE(convertText, element);
            convertE(other, element);
        }
        for (Element element : last.select("tr th")) {
            matchCovert(element, other);
        }
        matchCovert(last.selectFirst("h4"), other);
        if (urls != null) {
            for (Element a : last.select("td").last().select("a")) {
                urls.add(a.attr("href").replace("../", ""));
            }
        }
        writeDoc(file, doc);
    }

    private void convertCatalog(List<LinkInfo> list, Map<String, String> convertText, Map<String, String> other) throws IOException {
        File file;
        file = new File(Constants.DATA_PATH, "2300.html");
        Document doc = getDoc(file);
        matchCovert(getAList(doc.body()), convertText);
        writeDoc(file, doc);
        String title = other.get("title");
        for (String s : list.get(0).getData().values()) {
            File file1 = new File(Constants.MH_PATH, s);
            doc = getDoc(file1);
            Element last = doc.select("div#navi1 span").get(1);
            matchCovert(last, other);
            String text;
            for (Element element : doc.select("div[class^=panel]")) {
                Element a = element.selectFirst("a");
                text = a.text().trim();
                String key = text.contains(title) ? text.split(title)[0] : text;
                String value = convertText.get(key);
                if (value != null) {
                    a.text(text.replace(key, value));
                }
                last = element.select("tr th").last();

                if (last == null) {
                    System.out.println(text);
                } else {
                    matchCovert(last, other);
                }
            }
            writeDoc(file1, doc);
        }
    }


    public void convertEquipDetail(List<LinkInfo> infos, String url) throws IOException {
        Map<String, LinkInfo> map = new HashMap<>();
        for (LinkInfo linkInfo : infos) {
            map.put(linkInfo.getName(), linkInfo);
        }
        File file = new File(Constants.MH_PATH, url);
        Document doc = getDoc(file);
        for (Element element : doc.select("div[class^=panel]:has(table)")) {
            Element a = element.selectFirst("a");
            String text = a.text().trim();
            String equipUrl = a.attr("href").replace("../", "").trim();
            LinkInfo info = map.get(text);
            if (info != null) {
                Map<String, String> data = info.getData();
                for (Element e : element.select("td.left:has(img) a")) {
                    text = e.text().trim();
                    String value = data.get(text);
                    if (value != null) {
                        e.text(value);
                    }
                }
                convertEquipDetail(info, equipUrl);
            }
        }
        writeDoc(file, doc);
    }

    public void convertEquipDetail(LinkInfo info, String url) throws IOException {
        File file = new File(Constants.MH_PATH, url);
        Document doc = getDoc(file);
        Map<String, String> data = info.getData();
        for (Element e : doc.select("td.left:has(img)")) {
            String text = e.text().trim();
            String value = data.get(text);
            if (value != null) {
                Element a = e.selectFirst("img");
                Element img = new Element("img");
                String key = "style";
                img.attr(key, a.attr(key));
                key = "src";
                img.attr(key, a.attr(key));
                e.text(value);
                e.insertChildren(0, img);
            }
        }
        writeDoc(file, doc);
    }


    private void convertE(Map<String, String> map, Element e) {
        String text = e.text().trim();
        for (String s1 : map.keySet()) {
            if (text.contains(s1)) {
                text = text.replace(s1, map.get(s1));
                e.text(text);
                System.out.println(text);
                break;
            }
        }
    }
}
