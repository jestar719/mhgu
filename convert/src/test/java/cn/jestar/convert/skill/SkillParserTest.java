package cn.jestar.convert.skill;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
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
    public void convertSkillTest() throws IOException {
        File file = new File(Constants.DATA_PATH, "2200.html");
        File file1 = new File(Constants.IDA_PATH, "286087.html");
        Map<String, String> map = JsonUtils.fromString(new FileReader(mSkills), Map.class);
        mSkillParser.convertHtml(file1, map);
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