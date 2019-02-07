package cn.jestar.convert.collect;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import cn.jestar.convert.Constants;
import cn.jestar.convert.Type;
import cn.jestar.convert.bean.SkillBean;

import static org.junit.Assert.*;

/**
 * Created by 花京院 on 2019/2/7.
 */
public class SkillEffectCompilerTest {

    private String[] mPage;
    private String mTestText1;
    private String mTestText2;
    private String mTestText3;
    private SkillEffectCompiler mTestCompiler;
    private File mFile;
    private String mSkillFile;
    private String mEffectListFileName;

    @Before
    public void setUp() throws Exception {
        int start = 2201;
        int size = 7;
        mPage = new String[size];
        String temp = "data/%s.html";
        for (int i = 0; i < size; i++) {
            mPage[i] = String.format(temp, i + start);
        }
        mTestText1 = "  <td class=\"b\"><span style=\"color:red;\">火属性攻击弱化</span></td>";
        mTestText2 = "<td class=\"b\"><span style=\"color:red;\">-10</span></td>";
        mTestText3 = "<td class=\"left\"><span style=\"color:red;\">火属性的攻击*0.75倍</span></td>";
        mTestCompiler = new SkillEffectCompiler(Constants.MH_PATH + mPage[0]);
        mSkillFile = "技能数据汇总.json";
        mEffectListFileName="技能发动名称汇总.json";
        mFile = new File(Constants.TEMP_SUMMARY_PATH);
    }

    @Test
    public void getValue() throws Exception {
        assertEquals(mTestCompiler.getValue(mTestText2), -10);
    }

    @Test
    public void getText() throws Exception {
        assertEquals(mTestCompiler.getText(mTestText1), "火属性攻击弱化");
        assertEquals(mTestCompiler.getText(mTestText2), "-10");
        assertEquals(mTestCompiler.getText(mTestText3), "火属性的攻击*0.75倍");
    }

    @Test
    public void getList() throws Exception {
        List<SkillBean> skillBeans = new ArrayList<>();
        for (String s : mPage) {
            mTestCompiler = new SkillEffectCompiler(Constants.MH_PATH + s);
            skillBeans.addAll(mTestCompiler.getList());
        }
        Set<String> set=new LinkedHashSet<>();
        int id = (Type.SKILL << 8);
        for (SkillBean skillBean : skillBeans) {
            id++;
            skillBean.setId(id);
            List<SkillBean.SkillEffect> list = skillBean.getEffectList();
            for (SkillBean.SkillEffect effect : list) {
                set.add(effect.getName());
            }
        }
        mTestCompiler.write(skillBeans, new File(mFile,mSkillFile));
        mTestCompiler.write(set, new File(Constants.TEMP_TRANSLATION_PATH,mEffectListFileName));
    }

}
