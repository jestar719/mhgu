package cn.jestar.convert.index;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import cn.jestar.convert.Constants;
import cn.jestar.convert.bean.LinkInfo;
import cn.jestar.convert.utils.JsonUtils;
import cn.jestar.convert.utils.ParserUtils;
import cn.jestar.convert.utils.RegexUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by 花京院 on 2019/1/30.
 */
public class IndexCompilerTest {

    private IndexCompiler mCompiler;

    @Before
    public void init() {
        mCompiler = new IndexCompiler();
    }

    @Test
    public void testSetName() throws IOException {
        LinkInfo info = new LinkInfo();
        String text = "        <th class=\"th1\" colspan=\"2\">武器派生</th>";
        String text1 = "        <th class=\"th1\" colspan=\"12\">任务</th>";
        mCompiler.setName(null, text, info);
        assertEquals(info.getName(), "武器派生");
        mCompiler.setName(null, text1, info);
        assertEquals(info.getName(), "任务");
    }

    @Test
    public void testSetData() {
        LinkInfo linkInfo = new LinkInfo();
        String text = "        <td width=\"50%\"><a href=\"../data/2602.html\">笛旋律</a></td>";
        assertTrue(text.contains(IndexCompiler.TD));
        Matcher matcher = RegexUtils.getMatcher(text, IndexCompiler.LINK_REGEX);
        if (matcher.find()) {
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
        }
        assertTrue(!text.contains(IndexCompiler.SPLIT));
        mCompiler.setLink(text, linkInfo);
        assertTrue(!linkInfo.getData().isEmpty());
    }

    @Test
    public void onRead() throws Exception {
        mCompiler.readFile();
    }

    @Test
    public void testJsoup() throws IOException {
        File file = new File(Constants.INDEX);
        Document doc = ParserUtils.getDoc(file);
        Element body = doc.body();
        Elements div = body.select("table.t0");
        List<LinkInfo> list = new ArrayList<>(div.size());
        for (Element table : div) {
            String text = table.selectFirst("th.th1").text();
            LinkInfo info = new LinkInfo();
            list.add(info);
            info.setName(text);
            for (Element element : table.getElementsByTag("a")) {
                info.setData(element.text(), element.attr("href").replace("../", ""));
            }
        }
        JsonUtils.writeJson(new File(Constants.TEMP_SUMMARY_PATH, "index.json"), list);
    }

    @Test
    public void getEquipIndex() throws IOException {
        Document doc = ParserUtils.getDoc(new File(Constants.DATA_PATH, "2300.html"));
        List<LinkInfo> list = new ArrayList<>();
        Elements a = doc.select("div#navi1").first().getElementsByTag("a");
        LinkInfo info = new LinkInfo("equip_index");
        info.setData(ParserUtils.parseAList(a));
        list.add(info);
        a = doc.select("table.t1").first().getElementsByTag("a");
        info = new LinkInfo("all_equip");
        info.setData(ParserUtils.parseAList(a));
        list.add(info);
        JsonUtils.writeJson(new File(Constants.TEMP_SUMMARY_PATH, "equip_index.json"), list);
    }
}
