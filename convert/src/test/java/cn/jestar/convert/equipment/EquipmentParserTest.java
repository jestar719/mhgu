package cn.jestar.convert.equipment;

import com.google.gson.reflect.TypeToken;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jestar.convert.Constants;
import cn.jestar.convert.bean.BaseEquip;
import cn.jestar.convert.bean.FullEquip;
import cn.jestar.convert.bean.LinkInfo;
import cn.jestar.convert.utils.JsonUtils;

import static cn.jestar.convert.utils.ParserUtils.getDoc;
import static cn.jestar.convert.utils.ParserUtils.writeDoc;

/**
 * Created by 花京院 on 2019/9/4.
 */
public class EquipmentParserTest {

    private EquipmentParser mParser;

    @Before
    public void setUp() throws Exception {
        mParser = new EquipmentParser();
    }

    @Test
    public void parseEquipCatalog() throws Exception {
        mParser.parseEquipCatalog();
    }

    @Test
    public void parseEquipNameInPage() throws IOException {
//        mParser.parseEquipNameInPage();
        File file = new File(Constants.TEMP_TRANSLATED_PATH, EquipmentParser.EQUIP_DETAIL);
        Reader reader = new FileReader(file);
        FileReader reader1 = new FileReader(new File(Constants.TEMP_SUMMARY_PATH, EquipmentParser.EQUIP_INDEX_NAME));
        List<LinkInfo> list = JsonUtils.toList(reader1, LinkInfo.class);
        Map<String, String> info = list.get(list.size() - 1).getData();
        Set<Map.Entry<String, String>> entries = info.entrySet();
        String[] texts = new String[entries.size() * 2];
        int index = 0;
        for (Map.Entry<String, String> entry : entries) {
            texts[index] = entry.getKey();
            index++;
            texts[index] = entry.getValue();
            index++;
        }
        Type type = new TypeToken<Map<String, List<LinkInfo>>>() {
        }.getType();
        Map<String, List<LinkInfo>> t = JsonUtils.fromStringByType(reader, type);
        for (List<LinkInfo> infos : t.values()) {
            for (LinkInfo linkInfo : infos) {
                Map<String, String> data = linkInfo.getData();
                if (data.size() == 10) {
                    for (Map.Entry<String, String> entry : data.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        for (int i = 0; i < 2; i++) {
                            int i1 = i * 2;
                            String text = texts[i1];
                            String text1 = texts[i1 + 1];
                            if (key.endsWith(text) && value.endsWith(text1)) {
                                String text2 = texts[i1 == 0 ? i + 3 : i1 - 1];
                                value = value.replace(text1, text2);
                                data.put(key, value);
                            }
                        }
                    }
                }
            }
        }
        JsonUtils.writeJson(file, t);
    }

    private String replace(String text, String t1, String t2, String replace) {
        if (text.contains(t1) && text.contains(t2)) {
            text = text.replace(t2, replace);
        }
        return text;
    }

    @Test
    public void convertTitle() throws Exception {
        File file = new File(Constants.TEMP_SUMMARY_PATH, EquipmentParser.EQUIP_INDEX_NAME);
        List<LinkInfo> list = JsonUtils.toList(new FileReader(file), LinkInfo.class);
        Map<String, String> info = list.get(2).getData();
        Map<String, String> info1 = list.get(3).getData();
        Document doc;
        for (Map.Entry<String, String> entry : info.entrySet()) {
            File path = new File(Constants.MH_PATH, entry.getKey());
            doc = getDoc(path);
            String text = entry.getValue();
            String newText = info1.get(text);
            for (Element e : doc.select("div.f_min h3")) {
                e.text(e.text().replace(text, newText));
            }
            Element e = doc.selectFirst("h2.c_black");
            e.text(e.text().replace(text, newText));
            writeDoc(path, doc);
        }
    }


    @Test
    public void convertEquipDetail() throws IOException {
        Type type = new TypeToken<Map<String, List<LinkInfo>>>() {
        }.getType();
        File file = new File(Constants.TEMP_TRANSLATED_PATH, EquipmentParser.EQUIP_DETAIL);
        Map<String, List<LinkInfo>> map = JsonUtils.fromStringByType(new FileReader(file), type);
        File index = new File(Constants.TEMP_SUMMARY_PATH, EquipmentParser.EQUIP_INDEX_NAME);
        List<LinkInfo> infos = JsonUtils.toList(new FileReader(index), LinkInfo.class);
        LinkInfo info = infos.get(0);
        Set<Map.Entry<String, String>> set = info.getData().entrySet();
        ArrayList<Map.Entry<String, String>> entries = new ArrayList<>(set);
        for (Map.Entry<String, String> entry : entries) {
            mParser.convertEquipDetail(map.get(entry.getKey()), entry.getValue());
        }
    }

    @Test
    public void parseEquipDetail() throws IOException {
        File file = new File(Constants.TEMP_SUMMARY_PATH, EquipmentParser.EQUIP_INDEX_NAME);
        List<LinkInfo> infos = JsonUtils.toList(new FileReader(file), LinkInfo.class);
        List<BaseEquip> equipList = new ArrayList<>();
        List<BaseEquip.EquipSkill> equipSkills = new ArrayList<>();
        int rare = 0;
        int start = 1;
        int idStart = 0;
        for (String s : infos.get(0).getData().values()) {
            List<List<FullEquip>> lists = mParser.paserEquipDetail(s);
            int rare1 = lists.get(0).get(0).getRare();
            if (rare1 != rare) {
                start = 1;
                rare = rare1;
                idStart = rare * 10000;
            }
            int size = lists.size();
            for (int i = 0; i < size; i++) {
                List<FullEquip> equips = lists.get(i);
                int num = equips.size();
                for (int i1 = 0; i1 < num; i1++) {
                    FullEquip equip = equips.get(i1);
                    int id = equip.getId();
                    id = id + idStart + start * 10;
                    equip.setId(id);
                    List<BaseEquip.EquipSkill> skills = equip.getSkills();
                    for (BaseEquip.EquipSkill skill : skills) {
                        skill.setId(skill.getId() + id * 10);
                        skill.setEquipId(id);
                        equipSkills.add(skill);
                    }
                    equipList.add(equip.getBaseEquip());
                }
                start++;
            }
        }
        String name = String.format(EquipmentParser.CATALOG_PAGE_NAME, "equips");
        file = new File(Constants.TEMP_SUMMARY_PATH, name);
        JsonUtils.writeJson(file, equipList);
        name = String.format(EquipmentParser.CATALOG_PAGE_NAME, "equip_skills");
        file = new File(Constants.TEMP_SUMMARY_PATH, name);
        JsonUtils.writeJson(file, equipSkills);

    }
}