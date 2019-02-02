package cn.jestar.convert.weapon;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;

import cn.jestar.convert.Constants;
import cn.jestar.convert.bean.LinkInfo;
import cn.jestar.convert.utils.JsonUtils;
import cn.jestar.convert.utils.RegexUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by 花京院 on 2019/2/2.
 */
public class WeaponCompilerTest {

    @Test
    public void startTest() {
        String start1 = "      <tr class=\"view_panel_body a_cl220679 view_last_body  plb1 \">";
        String start2 = "      <tr class=\"view_panel_body a_cl219235  view_next_body plb4 plb9 \">";
        String start3 = "      <tr class=\"  plb4 plb9 \">";
        String start4 = "       <tr class=\"view_panel_body a_cl219235  plb4 plb9 \">";
        String regex = WeaponCompiler.START_REGEX;
        assertTrue(start1.matches(regex));
        assertTrue(start2.matches(regex));
        assertTrue(start3.matches(regex));
        assertTrue(start4.matches(regex));
    }


    @Test
    public void nameTest() {
        String nullText = "        ";
        String name1 = "贝尔达キャノン";
        String name2 = "贝尔达キャノン4";
        String name3 = "                <a href=\"../ida/219235.html\">贝尔达キャノン4</a>";
        String name4 = "铁刀【神乐】10";
        String name5 = "铁刀【神乐】8";
        assertTrue(nullText.trim().length() == 0);
        assertEquals(name1, RegexUtils.getTextWithoutNum(name1));
        assertEquals(name1, RegexUtils.getTextWithoutNum(name2));
        Matcher matcher = RegexUtils.getMatcher(name3, WeaponCompiler.NAME_REGEX);
        matcher.find();
        assertEquals(matcher.group(1), "ida/219235.html");
        assertEquals(matcher.group(2), name1);
        String num = RegexUtils.getTextWithoutNum(name5);
        assertEquals(RegexUtils.getTextWithoutNum(name4), num);
        System.out.println(num);
    }

    @Test
    public void getWeaponNameTest() throws FileNotFoundException {
        WeaponCompiler compiler = new WeaponCompiler(Constants.MH_PATH + "data/1901.html");
        compiler.readFile();
        System.out.println(compiler.getMap());
    }

    @Test
    public void getWeaponNameList() throws FileNotFoundException {
        WeaponCompiler compiler = new WeaponCompiler();
        File file = new File(Constants.TEMP_SUMMARY_PATH);
        File source = new File(file, "武器一览.json");
        List<LinkInfo> infos = JsonUtils.toList(new FileReader(source), LinkInfo.class);
        for (LinkInfo info : infos) {
            String name = info.getName();
            TreeMap<String, String> map = new TreeMap<>();
            for (String s : info.getData().values()) {
                compiler.setFile(Constants.MH_PATH + s);
                compiler.readFile();
                map.putAll(compiler.getMap());
            }
            File file1 = new File(file, "weapon\\" + name + ".json");
            compiler.write(map, file1);
        }
    }
}
