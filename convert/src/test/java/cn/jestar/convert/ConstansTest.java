package cn.jestar.convert;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by 花京院 on 2019/1/27.
 */
public class ConstansTest {
    @Test
    public void testPath() {
        assertTrue( new File(Constans.BASE).exists());
        assertTrue( new File(Constans.MH_PATH).exists());
        assertTrue( new File(Constans.CONVERT_PATH).exists());
        assertTrue( new File(Constans.TEMP_PATH).exists());
        assertTrue( new File(Constans.INDEX).exists());
        assertTrue( new File(Constans.DATA_PATH).exists());
        assertTrue( new File(Constans.IDA_PATH).exists());
    }
    @Test
    public void test(){
        float money=260000;
        int yearPay=1700*12;
        boolean flag=true;
        int year=0;
        while (flag){
           money=money*1.07f-yearPay;
           year++;
           flag=money>0;
        }
        System.out.println(year);
    }

}
