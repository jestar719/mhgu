package cn.jestar.convert.index;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.regex.Matcher;

import cn.jestar.convert.bean.LinkInfo;
import cn.jestar.convert.utils.RegexUtils;

import static org.junit.Assert.*;

/**
 * Created by 花京院 on 2019/1/30.
 */
public class IndexCompilerTest {

    private IndexCompiler mCompiler;

    @Before
    public void init(){
        mCompiler = new IndexCompiler();
    }
    @Test
    public void testSetName() throws IOException {
        LinkInfo info = new LinkInfo();
        String text="        <th class=\"th1\" colspan=\"2\">武器派生</th>";
        String text1="        <th class=\"th1\" colspan=\"12\">任务</th>";
        mCompiler.setName(null,text,info);
        assertEquals(info.getName(),"武器派生");
        mCompiler.setName(null,text1,info);
        assertEquals(info.getName(),"任务");
    }

    @Test
    public void testSetData(){
        LinkInfo linkInfo = new LinkInfo();
        String text="        <td width=\"50%\"><a href=\"../data/2602.html\">笛旋律</a></td>";
        assertTrue(text.contains(IndexCompiler.TD));
        Matcher matcher = RegexUtils.getMatcher(text, IndexCompiler.LINK_REGEX);
        if (matcher.find()){
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
        }
        assertTrue(!text.contains(IndexCompiler.SPLIT));
        mCompiler.setLink(text,linkInfo);
        assertTrue(!linkInfo.getData().isEmpty());
    }
    @Test
    public void onRead() throws Exception {
        mCompiler.readFile();
    }



}
