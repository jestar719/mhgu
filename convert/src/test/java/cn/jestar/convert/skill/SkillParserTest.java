package cn.jestar.convert.skill;

import com.google.gson.reflect.TypeToken;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jestar.convert.Constants;
import cn.jestar.convert.bean.LinkInfo;
import cn.jestar.convert.bean.SkillBean;
import cn.jestar.convert.bean.SkillDetailBean;
import cn.jestar.convert.bean.SkillJewelryBean;
import cn.jestar.convert.utils.JsonUtils;
import cn.jestar.convert.utils.ParserUtils;

import static cn.jestar.convert.utils.ParserUtils.getDoc;
import static cn.jestar.convert.utils.ParserUtils.writeDoc;

/**
 * Created by 花京院 on 2019/8/29.
 */
public class SkillParserTest {

    private SkillParser mSkillParser;
    private File mSkills;
    private File mFile;

    @Before
    public void init() {
        mSkillParser = new SkillParser();
        mSkills = new File(Constants.TEMP_TRANSLATED_PATH, "skill/skills.json");
    }

    @Test
    public void parseSkillNameTest() throws Exception {
        Map map = mSkillParser.parseSkillName();
        System.out.println(map);
    }

    @Test
    public void parseSkillTest() throws Exception {
        File file = new File(Constants.MH_PATH, "ida/286037.html");
        SkillBean skillBean = mSkillParser.parseSkill(file, new SkillBean());
        System.out.println(JsonUtils.toString(skillBean));
    }

    @Test
    public void getAllSkill() throws IOException {
        File file = new File(Constants.TEMP_SUMMARY_PATH, "skill/skills.json");
        List<SkillBean> beans = JsonUtils.toList(new FileReader(file), SkillBean.class);
        beans.sort((o1,o2)-> {
                int result = o1.getType() - o2.getType();
                return result==0?o1.getName().compareTo(o2.getName()):result;
        });
        int id=1;
        for (SkillBean bean : beans) {
            bean.setId(id++);
            bean.setUrl(bean.getUrl().replace("../",""));
        }
        JsonUtils.writeJson(file, beans);
    }

    @Test
    public void createSkillType() throws IOException {
        File file = new File(Constants.TEMP_SUMMARY_PATH, "skill/skills.json");
        List<SkillBean> skillBeans = JsonUtils.toList(new FileReader(file), SkillBean.class);
        file = new File(Constants.TEMP_SUMMARY_PATH, "skill/Jewelrys.json");
        List<SkillJewelryBean> jewelryBeans = JsonUtils.toList(new FileReader(file), SkillJewelryBean.class);
        Collections.sort(jewelryBeans);
        HashMap<String, LinkedList<SkillJewelryBean>> map = new HashMap<>();
        for (SkillJewelryBean bean : jewelryBeans) {
            String name = bean.getSkill();
            LinkedList<SkillJewelryBean> list = map.get(name);
            if (list == null) {
                list = new LinkedList<>();
                map.put(name, list);
            }
            list.add(bean);
        }
        ArrayList<SkillDetailBean> list = new ArrayList<>();
        SkillDetailBean detail;
        for (SkillBean bean : skillBeans) {
            detail = new SkillDetailBean();
            String name = bean.getName();
            LinkedList<SkillJewelryBean> jList = map.get(name);
            boolean isEmpty = jList == null;
            SkillJewelryBean skillJewelryBean = isEmpty ? null : jList.peekLast();
            detail.setSkillBean(bean, skillJewelryBean, isEmpty ? 0 : jList.size());
            list.add(detail);
        }
        JsonUtils.writeJson(new File(Constants.TEMP_SUMMARY_PATH, "skill/skills.json"), skillBeans);
        list.sort((o1, o2) -> {
            int result = o1.getType() - o2.getType();
            result = result == 0 ? o2.getMaxValue() - o1.getMaxValue() : result;
            result = result == 0 ? o2.getLeftMaxValue() + o2.getRightMaxValue() - o1.getLeftMaxValue() - o1.getRightMaxValue() : result;
            result = result == 0 ? o1.getSlotNum() - o2.getSlotNum() : result;
            result = result == 0 ? o2.getSlotValue() - o1.getSlotValue() : result;
            result = result == 0 ? o2.getName().compareTo(o1.getName()) : result;
            return result;
        });
        file = new File(Constants.TEMP_SUMMARY_PATH, "skill/skill_detail.json");
        JsonUtils.writeJson(file, list);
    }

    @Test
    public void createSkillDetailHtml() throws IOException {
        File file = new File(Constants.DATA_PATH, "2208.html");
        Document doc = getDoc(file);
        File json = new File(Constants.TEMP_SUMMARY_PATH, "skill/skill_detail.json");
        List<SkillDetailBean> list = JsonUtils.toList(new FileReader(json), SkillDetailBean.class);
        int type = -1;
        Elements select = doc.select("table.t1 tbody");
        for (Element element : select) {
            element.select("tr:has(td)").remove();
        }
        addSkillListInTable(list, type, select);
        writeDoc(file, doc);
    }

    private void addSkillListInTable(List<SkillDetailBean> list, int type, Elements select) {
        Element element = null;
        for (SkillDetailBean bean : list) {
            int beanType = bean.getType();
            if (type != beanType) {
                type = beanType;
                element = select.get(type);
            }
            Element tr = new Element("tr");
            Element td = getTd(false);
            Element a = new Element("a");
            a.text(bean.getName());
            String url = bean.getUrl();
            a.attr("href", url);
            td.appendChild(a);
            tr.appendChild(td);
            String temp = "<span style=\"color:#0033ff;\">%s</span> | <span style=\"color:#ff6600;\">%s</span>";
            String maxValue = String.format(temp, bean.getLeftMaxValue(), bean.getRightMaxValue());
            td = getTd(true);
            td.append(maxValue);
            tr.appendChild(td);
            addTd(String.valueOf(bean.getJewelryNum()), true, tr);
            addTd(bean.getJewelryName(), false, tr);
            int slotValue = bean.getSlotValue();
            addTd(String.valueOf(slotValue), true, tr);
            addTd(String.valueOf(bean.getSlotNum()), true, tr);
            String value = slotValue == 2 ? "<span style=\"color:#0011ff;\">Yes</span>" : "No";
            td = getTd(true);
            td.append(value);
            tr.appendChild(td);
            element.appendChild(tr);
        }
    }

    private Element getTd(boolean isDefault) {
        Element td = new Element("td");
        td.attr("width", isDefault ? "12%" : "20%");
        return td;
    }

    private void addTd(String value, boolean isDefault, Element tr) {
        if (value == null) {
            value = "";
        }
        tr.appendChild(getTd(isDefault).text(value));
    }

    @Test
    public void getJwe() throws IOException {
        List<SkillJewelryBean> list = mSkillParser.parseJewelry();
        File file = new File(Constants.TEMP_SUMMARY_PATH, "skill/Jewelrys.json");
        JsonUtils.writeJson(file, list);
    }


    @Test
    public void splitSkillConvertText() throws IOException {
        String skillName = "SkillName";
        String effectName = "EffectName";
        Map<String, String> map = JsonUtils.fromString(new FileReader(mSkills), Map.class);
        File file = new File(Constants.TEMP_TRANSLATED_PATH, "skill/skillNames.json");
        Type type = new TypeToken<HashMap<String, List<String>>>() {
        }.getType();
        Map<String, List<String>> names = JsonUtils.fromStringByType(new FileReader(file), type);
        Map<String, Map<String, String>> map1 = new LinkedHashMap<>();
        Map<String, String> map2 = new LinkedHashMap<>();
        for (String key : names.get(skillName)) {
            String value = map.get(key);
            if (value != null) {
                map2.put(key, value);
            }
        }
        map1.put(skillName, map2);
        map2 = new LinkedHashMap<>();
        for (String key : names.get(effectName)) {
            String value = map.get(key);
            if (value != null) {
                map2.put(key, value);
            }
        }
        map1.put(effectName, map2);
        JsonUtils.writeJson(new File(Constants.TEMP_TRANSLATED_PATH, "skill/full_skills.json"), map1);
    }

    @Test
    public void convertSkillTest() throws IOException {
        FileReader reader = new FileReader(new File(Constants.TEMP_TRANSLATED_PATH, "skill/jwd.json"));
        Type type = new TypeToken<Map<String, Map<String, String>>>() {
        }.getType();
        Map<String, Map<String, String>> map = JsonUtils.fromStringByType(reader, type);
        Map<String, String> name = map.get("name");
        Map<String, String> map1 = JsonUtils.fromString(new FileReader(mSkills), Map.class);
        map1.putAll(name);
        for (String s : mSkillParser.parseSkillName().values()) {
            mSkillParser.convertSkillInDetail(new File(Constants.MH_PATH, s), map1);
        }
    }

    @Test
    public void convertSkillPlus() throws IOException {
        File file = new File(Constants.TEMP_SUMMARY_PATH, "skill_index.json");
        Type type = new TypeToken<Map<String, Map<String, String>>>() {
        }.getType();
        Map<String, Map<String, String>> map = JsonUtils.fromStringByType(new FileReader(file), type);
        Map<String, String> skills = map.get("skill");
        for (String name : skills.keySet()) {
            String url = skills.get(name);
            skillPlus(name, url);
        }
    }

    private void skillPlus(String name, String url) throws IOException {
        File file1 = new File(Constants.MH_PATH, url);
        Document doc = getDoc(file1);
        Element element = doc.selectFirst("td.b");
        if (!element.text().trim().equals(name)) {
            element.text(name);
            element = doc.selectFirst("h2");
            String h2 = element.text();
            String s = h2.split(" - ")[0].trim();
            element.text(h2.replace(s, name));
            writeDoc(file1, doc);
        }
    }

    @Test
    public void parseSkillEffect() throws IOException {
        Type type = new TypeToken<Map<String, Map<String, String>>>() {
        }.getType();
        File file = new File(Constants.TEMP_SUMMARY_PATH, "skill_index.json");
        Map<String, Map<String, String>> map = JsonUtils.fromStringByType(new FileReader(file), type);
        Map<String, String> skill = map.get("skill");
        Set<String> set = skill.keySet();
        ArrayList<LinkInfo> list = new ArrayList<>(set.size());
        for (String s : set) {
            list.add(mSkillParser.parseSkillEffect(skill.get(s)));
        }
        file = new File(Constants.TEMP_TRANSLATED_PATH, "skill/skill_effect.json");
        JsonUtils.writeJson(file, list);
    }


    /**
     * 翻译一览和各分类的技能
     *
     * @throws IOException
     */
    @Test
    public void convertSkillTypeTest() throws IOException {
        String child = "%s.html";
        Map<String, String> map = JsonUtils.fromString(new FileReader(mSkills), Map.class);
        for (int i = 2201; i <= 2207; i++) {
            String page = String.format(child, i);
            mSkillParser.convertSkillInType(new File(Constants.DATA_PATH, page), map);
        }
        mSkillParser.convertHtml(new File(Constants.DATA_PATH, String.format(child, 2200)), map);
    }

    /**
     * 翻译防具相关的技能
     *
     * @throws IOException
     */
    @Test
    public void convertSkillInEquipTest() throws IOException {
        Map<String, String> map = JsonUtils.fromString(new FileReader(mSkills), Map.class);
        File file = new File(Constants.TEMP_SUMMARY_PATH, "equip_index.json");
        List<LinkInfo> list = JsonUtils.toList(new FileReader(file), LinkInfo.class);
        LinkInfo info = list.get(0);
        Collection<String> values = info.getData().values();
//        for (String value : values) {
//            mSkillParser.convertHtml(new File(Constants.MH_PATH, value), map);
//        }
        for (String s : list.get(3).getData().values()) {
            mSkillParser.convertSkillInEquip(new File(Constants.MH_PATH, s), map);
        }
    }

    /**
     * 翻译防具相关的技能
     *
     * @throws IOException
     */
    @Test
    public void convertSkillInJwerldyTest() throws IOException {
        File file = new File(Constants.TEMP_TRANSLATED_PATH, "skill/full_skills.json");
        File file1 = new File(Constants.TEMP_TRANSLATED_PATH, "skill/jwd.json");
        Type type = new TypeToken<Map<String, Map<String, String>>>() {
        }.getType();
        Map<String, Map<String, String>> map = JsonUtils.fromStringByType(new FileReader(file), type);
        Map<String, Map<String, String>> jwdMap = JsonUtils.fromStringByType(new FileReader(file1), type);
        Map<String, String> texts = map.get("SkillName");
        Map<String, String> jwd = jwdMap.get("name");
        texts.putAll(jwd);
        HashSet<String> set = new HashSet<>();
        for (int i = 2580; i <= 2582; i++) {
            File file2 = new File(Constants.DATA_PATH, i + ".html");
            mSkillParser.convertHtml(file2, texts);
        }
        for (String index : jwdMap.get("index").values()) {
            File file2 = new File(Constants.MH_PATH, index);
            mSkillParser.convertSkillInJwerldy(file2, texts, set);
        }
        for (String s : set) {
            File file2 = new File(Constants.MH_PATH, s);
            Document doc = ParserUtils.getDoc(file2);
            Elements a = doc.getElementsByTag("td").last().select("a");
            mSkillParser.convertInElements(jwd, a);
            writeDoc(file2, doc);
        }
    }

    /**
     * 狩技翻译
     *
     * @throws IOException
     */
    @Test
    public void fightSkill() throws IOException {
        File file = new File(Constants.DATA_PATH, "1847.html");
        File file1 = new File(Constants.TEMP_TRANSLATED_PATH, "skill/fight_skill.json");
        Type type = new TypeToken<Map<String, Map<String, String>>>() {
        }.getType();
        Map<String, Map<String, String>> map = JsonUtils.fromStringByType(new FileReader(file1), type);
        Map<String, String> names = map.get("name");
        Document doc = getDoc(file);
        Elements select = doc.select("td.b[rowspan]:not([style])");
        for (Element element : select) {
            String text = element.text().trim();
            String s = names.get(text);
            if (s != null) {
                element.text(s);
            }
        }
        writeDoc(file, doc);
        for (String link : map.get("link").values()) {
            file = new File(Constants.MH_PATH, link);
            doc = getDoc(file);
            Elements a = doc.select("a");
            for (Element element : a) {
                String text = element.text().trim();
                String s = names.get(text);
                if (s != null) {
                    element.text(s);
                }
            }
            writeDoc(file, doc);
        }
        JsonUtils.writeJson(file1, map);
    }

    /**
     * 技能效果翻译
     */
    @Test
    public void convertSkillEffect() throws IOException {
        File file = new File(Constants.TEMP_TRANSLATED_PATH, "skill/skill_effect.json");
        List<LinkInfo> list = JsonUtils.toList(new FileReader(file), LinkInfo.class);
        Map<String, String> map = new HashMap<>();
        for (LinkInfo info : list) {
            map.putAll(info.getData());
        }
        Set<String> set = new HashSet<>();
        for (LinkInfo info : list) {
            set.addAll(mSkillParser.convertSkillEffect(info));
        }
        for (String url : set) {
            mSkillParser.convertSkillEffect(url, map);
        }
        for (int i = 2201; i < 2207; i++) {
            File file1 = new File(Constants.DATA_PATH, i + ".html");
            mSkillParser.convertSkillEffectInCatlog(file1,map);
        }
    }
}