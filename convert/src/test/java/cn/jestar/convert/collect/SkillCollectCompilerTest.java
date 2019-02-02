package cn.jestar.convert.collect;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by 花京院 on 2019/1/31.
 */
public class SkillCollectCompilerTest {
    @Test
    public void onRead() throws Exception {
        new SkillCollectCompiler().readFile();
    }

}
