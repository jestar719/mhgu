package cn.jestar.convert.utils;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jestar.convert.index.IndexCompiler;
import cn.jestar.convert.index.UrlCompiler;

import static org.junit.Assert.*;

/**
 * Created by 花京院 on 2019/1/29.
 */
public class RegexUtilsTest {
    @Test
    public void getTextWithoutNum() throws Exception {
        String test="这是测试文本12";
        String s = RegexUtils.getTextWithoutNum(test);
        assertNotNull(s);
        String test1="这是测试文本";
        s = RegexUtils.getTextWithoutNum(test1);
        assertEquals(s,test1);
    }

    @Test
    public void test(){
       String source="        <th class=\"th1\" colspan=\"2\">武器派生</th>";
       String regex= IndexCompiler.NAME_REGEX;
        String text = RegexUtils.getMatchText(source, regex);
        assertNotNull(text);
        assertEquals(text.length(),4);
    }

    @Test
    public void test1(){
        String source="        <td><a href=\"../data/1906.html\">长枪1</a> / <a href=\"../data/2888.html\">长枪2</a> / <a href=\"../data/2706.html\">最終</a></td>";
        String regex= IndexCompiler.LINK_REGEX;
        Matcher matcher = Pattern.compile(regex).matcher(source);
       assertTrue(matcher.find());
    }

    @Test
    public void testGetNum(){
        String text="1.json";
        String s = RegexUtils.getMatchNum(text);
        assertTrue(s!=null);
        assertEquals(s,"1");
    }
    @Test
    public void testMath(){
        String start="<table class=\"t1\">";
        assertTrue(start.contains(UrlCompiler.START_FLAG));
    }

}
