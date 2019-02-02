package cn.jestar.convert.index;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by 花京院 on 2019/1/30.
 */
public class CatRiceCompilerTest {
    @Test
    public void onRead() throws Exception {
        new CatRiceCompiler().readFile();
    }

}
