package cn.jestar.convert.skill;

import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jestar.convert.Constants;
import cn.jestar.convert.bean.SkillBean;
import cn.jestar.convert.utils.JsonUtils;

/**
 * Created by 花京院 on 2019/8/29.
 */
public class SkillParserTest {

    private SkillParser mSkillParser;
    private File mSkills;

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
        SkillBean skillBean = mSkillParser.parseSkill(file);
        System.out.println(JsonUtils.toString(skillBean));
    }

    @Test
    public void getAllSkill() throws IOException {
        Collection<String> values = mSkillParser.parseSkillName().values();
        Set<String> skillNameSet = new LinkedHashSet<>();
        Set<String> effectNameSet = new LinkedHashSet<>();
        Map<String, Set<String>> map = new HashMap<>();
        map.put("SkillName", skillNameSet);
        map.put("EffectName", effectNameSet);
        for (String value : values) {
            SkillBean skillBean = mSkillParser.parseSkill(new File(Constants.MH_PATH, value));
            skillNameSet.add(skillBean.getName());
            for (SkillBean.SkillEffect effect : skillBean.getEffectList()) {
                String name = effect.getName();
                int index;
                if ((index = name.indexOf("+")) > 0 || (index = name.indexOf("-")) > 0) {
                    name = name.substring(0, index);
                }
                effectNameSet.add(name);
            }
        }
        JsonUtils.writeJson(new File(Constants.TEMP_TRANSLATED_PATH, "skill/skillNames.json"), map);
    }
    @Test
    public void getJwe() throws IOException {
        File json = new File(Constants.TEMP_TRANSLATED_PATH, "skill/jwd.json");
        Map<String, String> map = JsonUtils.fromString(new FileReader(json), Map.class);
        Map<String, String> jwerd = mSkillParser.parseJwerd();
        Set<String> strings = jwerd.keySet();
        HashMap<String, String> hashMap = new HashMap<>();
        for (String string : strings) {
            String s = string.substring(0, string.length() - 3);
            String s1 = map.get(s);
            System.out.println(String.format("%s %s", s, s1));
            if (s1 != null) {
                hashMap.put(string, string.replace(s, s1));
            }
        }
        Map<String, Map<String, String>> list = new HashMap<>();
        list.put("index", jwerd);
        list.put("name", hashMap);
        JsonUtils.writeJson(json, list);
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

}