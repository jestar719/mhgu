package cn.jestar.convert.weapon;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import cn.jestar.convert.bean.TranslatedBean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by 花京院 on 2019/2/4.
 */
public class WeaponConvertorTest {


    @Test
    public void test() throws Exception {
        String name = "太刀";
        WeaponConvertor convertor = new WeaponConvertor(name);
        System.out.println(convertor.checkTranslatedText());
    }

    @Test
    public void makeBean() throws Exception {
        String name = "太刀";
        WeaponConvertor convertor = new WeaponConvertor(name);
        convertor.makeBean();
    }

    @Test
    public void testRegex() throws Exception {
        String regex = WeaponConvertor.REGEX;
        String text1 = "<a href=\"../ida/189724.html\">龙骨【大】</a> x1<br>";
        String text2 = "<a href=\"../ida/219923.html\">辉龙石</a> x10<br>";
        String text3 = "<span style=\"background-color:#FFEFD3;\">入手端材：<a href=\"../ida/229914.html\">骨的上端材</a> x2</span><br>";
        assertTrue(text1.matches(regex));
        assertTrue(text2.matches(regex));
        assertFalse(text3.matches(regex));
        WeaponConvertor convertor = new WeaponConvertor(null);
        String url = "ida/230223.html";
        TreeSet<String> set = new TreeSet<>();
        StringBuilder text = convertor.getText(url, set);
        System.out.println(set);
    }

    @Test
    public void translateStepTest() throws Exception {
        String name = "太刀";
        WeaponConvertor convertor = new WeaponConvertor(name);
        TranslatedBean bean = convertor.getBean();
        Map<String, String> texts = bean.getTexts();
        List<String> list = convertor.getList(texts);
        String[] urls = new String[]{
                "data/1901.html",
                "data/2883.html",
                "data/2701.html",
        };
        for (String url : urls) {
            StringBuilder text = convertor.getText(url, null);
            convertor.translation(text, texts, list);
            convertor.write(text.toString(), url);
        }
    }

    @Test
    public void translateTaiDao() throws Exception {
        new WeaponConvertor("太刀").translation();
    }
}
