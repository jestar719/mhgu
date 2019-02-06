package cn.jestar.convert.weapon;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import cn.jestar.convert.bean.TranslatedBean;
import cn.jestar.convert.utils.RegexUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by 花京院 on 2019/2/4.
 */
public class WeaponConvertorTest {

    private String mName;
    private WeaponConvertor mConvertor;
    private String[] mUrls;

    @Before
    public void init() {
        mName = "盾斧";
        mConvertor = new WeaponConvertor(mName);
        mUrls = new String[]{
                "data/1909.html",
                "data/2709.html"
        };
    }

    /**
     * 检查翻译文本
     *
     * @throws Exception
     */
    @Test
    public void checkTranslatedTextTest() throws Exception {
        System.out.println(mConvertor.checkTranslatedText());
    }

    /**
     * 创建Bean
     *
     * @throws Exception
     */
    @Test
    public void makeBean() throws Exception {
        mConvertor.makeBean(Arrays.asList(mUrls));
    }

    /**
     * 验证正则
     *
     * @throws Exception
     */
    @Test
    public void testRegex() throws Exception {
        String regex = WeaponConvertor.REGEX;
        String regex1 = WeaponConvertor.REGEX1;
        String text1 = "<a href=\"../ida/189724.html\">龙骨【大】</a> x1<br>";
        String text2 = "<a href=\"../ida/219923.html\">辉龙石</a> x10<br>";
        String text3 = "<span style=\"background-color:#FFEFD3;\">入手端材：<a href=\"../ida/229914.html\">骨的上端材</a> x2</span><br>";
        assertTrue(text1.matches(regex));
        assertTrue(text2.matches(regex));
        assertTrue(text3.matches(regex1));
        assertEquals(RegexUtils.getMatchText(text3, regex1), "ida/229914.html");
        WeaponConvertor convertor = new WeaponConvertor(null);
        String url = "ida/230223.html";
        TreeSet<String> set = new TreeSet<>();
        StringBuilder text = convertor.getText(url, set);
        System.out.println(set);
    }

    /**
     * 翻译验证
     *
     * @throws Exception
     */
    @Test
    public void translateStepTest() throws Exception {
        TranslatedBean bean = mConvertor.getBean();
        Map<String, String> texts = bean.getTexts();
        List<String> list = mConvertor.getList(texts);
        for (String url : mUrls) {
            StringBuilder text = mConvertor.getText(url, null);
            mConvertor.translation(text, texts, list);
            mConvertor.write(text.toString(), url);
        }
    }

    /**
     * 太刀翻译
     *
     * @throws Exception
     */
    @Test
    public void translateTaiDao() throws Exception {
        new WeaponConvertor("太刀").translation();
    }

    /**
     * 太刀翻译
     *
     * @throws Exception
     */
    @Test
    public void translateDunFu() throws Exception {
        new WeaponConvertor("盾斧").translation();
    }
}
