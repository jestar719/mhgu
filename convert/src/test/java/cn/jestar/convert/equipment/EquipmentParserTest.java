package cn.jestar.convert.equipment;

import com.google.gson.reflect.TypeToken;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jestar.convert.Constants;
import cn.jestar.convert.bean.LinkInfo;
import cn.jestar.convert.utils.JsonUtils;

import static cn.jestar.convert.utils.ParserUtils.getDoc;
import static cn.jestar.convert.utils.ParserUtils.writeDoc;

/**
 * Created by 花京院 on 2019/9/4.
 */
public class EquipmentParserTest {

    private EquipmentParser mParser;

    @Before
    public void setUp() throws Exception {
        mParser = new EquipmentParser();
    }

    @Test
    public void parseEquipCatalog() throws Exception {
        mParser.parseEquipCatalog();
    }

    @Test
    public void parseEquipNameInPage() throws IOException {
//        mParser.parseEquipNameInPage();
        File file = new File(Constants.TEMP_TRANSLATED_PATH, EquipmentParser.EQUIP_DETAIL);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        FileReader reader1 = new FileReader(new File(Constants.TEMP_SUMMARY_PATH, EquipmentParser.EQUIP_INDEX_NAME));
        List<LinkInfo> list = JsonUtils.toList(reader1, LinkInfo.class);
        Map<String, String> info = list.get(list.size() - 1).getData();
        Set<Map.Entry<String, String>> entries = info.entrySet();
        String[] texts = new String[entries.size() * 2];
        int index = 0;
        for (Map.Entry<String, String> entry : entries) {
            texts[index] = entry.getKey();
            index++;
            texts[index] = entry.getValue();
            index++;
        }
        String line = null;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            line = replace(line, texts[0], texts[1], texts[3]);
            line = replace(line, texts[2], texts[3], texts[1]);
            builder.append(line);
        }
        FileWriter writer = new FileWriter(file);
        writer.write(builder.toString());
        reader.close();
        writer.close();
//        JsonUtils.writeJson(file, equip);
    }

    private String replace(String text, String t1, String t2, String replace) {
        if (text.contains(t1) && text.contains(t2)) {
            text = text.replace(t2, replace);
        }
        return text;
    }

    @Test
    public void convertTitle() throws Exception {
        File file = new File(Constants.TEMP_SUMMARY_PATH, EquipmentParser.EQUIP_INDEX_NAME);
        List<LinkInfo> list = JsonUtils.toList(new FileReader(file), LinkInfo.class);
        Map<String, String> info = list.get(2).getData();
        Map<String, String> info1 = list.get(3).getData();
        Document doc;
        for (Map.Entry<String, String> entry : info.entrySet()) {
            File path = new File(Constants.MH_PATH, entry.getKey());
            doc = getDoc(path);
            String text = entry.getValue();
            String newText = info1.get(text);
            for (Element e : doc.select("div.f_min h3")) {
                e.text(e.text().replace(text, newText));
            }
            Element e = doc.selectFirst("h2.c_black");
            e.text(e.text().replace(text, newText));
            writeDoc(path, doc);
        }
    }


    @Test
    public void convertEquipDetail() throws IOException {
        Type type = new TypeToken<Map<String, List<LinkInfo>>>() {
        }.getType();
        File file = new File(Constants.TEMP_TRANSLATED_PATH, EquipmentParser.EQUIP_DETAIL);
        Map<String, List<LinkInfo>> map = JsonUtils.fromStringByType(new FileReader(file), type);
        File index = new File(Constants.TEMP_SUMMARY_PATH, EquipmentParser.EQUIP_INDEX_NAME);
        List<LinkInfo> infos = JsonUtils.toList(new FileReader(index), LinkInfo.class);
        LinkInfo info = infos.get(0);
        Set<Map.Entry<String, String>> set = info.getData().entrySet();
        ArrayList<Map.Entry<String, String>> entries = new ArrayList<>(set);
        Map.Entry<String, String> entry = entries.get(1);
        mParser.convertEquipDetail(map.get(entry.getKey()), entry.getValue());
    }
}