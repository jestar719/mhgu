package cn.jestar.convert;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 * 常量的测试
 * Created by 花京院 on 2019/1/27.
 */
public class ConstantsTest {

    public void testPath() {
        assertTrue(new File(Constants.BASE).exists());
        assertTrue(new File(Constants.MH_PATH).exists());
        assertTrue(new File(Constants.TEMP_PATH).exists());
        assertTrue(new File(Constants.INDEX).exists());
        assertTrue(new File(Constants.DATA_PATH).exists());
        assertTrue(new File(Constants.IDA_PATH).exists());
    }
}
