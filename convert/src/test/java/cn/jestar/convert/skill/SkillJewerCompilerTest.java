package cn.jestar.convert.skill;

import org.junit.Test;

import java.io.File;

import cn.jestar.convert.Constants;

/**
 * Created by 花京院 on 2019/2/25.
 */
public class SkillJewerCompilerTest {
    @Test
    public void getMap() throws Exception {
        String urlTemp = "data/%s.html";
        SkillJewerCompiler compiler = new SkillJewerCompiler();
        for (int i = 2580; i <=2582; i++) {
            String format = String.format(urlTemp, i);
            System.out.println(format);
            File file = new File(Constants.MH_PATH, format);
            compiler.setFile(file);
            compiler.getMap();
        }
        compiler.writeMap();
    }

}
