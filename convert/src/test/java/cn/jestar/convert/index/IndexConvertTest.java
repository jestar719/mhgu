package cn.jestar.convert.index;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cn.jestar.convert.Constants;
import cn.jestar.convert.Type;
import cn.jestar.convert.bean.DbBean;
import cn.jestar.convert.bean.LinkInfo;
import cn.jestar.convert.utils.RegexUtils;

import static org.junit.Assert.assertTrue;

/**
 * Created by 花京院 on 2019/1/27.
 */
public class IndexConvertTest {

    private IndexConvert mConvert;
    private File mFile;
    private List<LinkInfo> mRead;

    @Before
    public void setUp() throws Exception {
        mConvert = new IndexConvert();
        mFile = new File(Constants.TEMP_PATH);
    }


    @Test
    public void read() throws Exception {
        File source = new File(mFile, "infos");
        source = new File(source, "8.json");
        mRead = mConvert.read(source);
        assertTrue(mRead != null);
        assertTrue(!mRead.isEmpty());
        System.out.println(mRead);
    }

    @Test
    public void linkInfo2DbBean() throws Exception {
        read();
        LinkInfo linkInfo = mRead.get(0);
        Map<String, String> data = linkInfo.getData();
        ArrayList<String> list = new ArrayList<>(data.keySet());
        Collections.sort(list);
        System.out.println(list);
        List<DbBean> beans = mConvert.linkInfo2DbBean(mRead, Type.QUEST);
        assertTrue(beans != null);
        assertTrue(!beans.isEmpty());
    }

    /**
     * 读取json.转换并写入指定的文件
     *
     * @throws Exception
     */
    @Test
    public void convert2Json() throws Exception {
        File source = new File(mFile, "infos");
        File target = new File(mFile, "BeanList");
        for (File file : source.listFiles()) {
            String name = file.getName();
            int type = Integer.parseInt(RegexUtils.getMatchNum(name));
            mConvert.convert2Json(file, new File(target, name), type);
        }
    }

    @Test
    public void copy() throws IOException {
        File source = new File(mFile, "BeanList");
        File target = new File(Constants.WRAPPER_INDEX_PATH);
        File[] files = source.listFiles();
        for (File file : files) {
            String name = file.getName();
            File file1 = new File(target, name);
            FileInputStream in = new FileInputStream(file);
            FileOutputStream out = new FileOutputStream(file1);
            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            out.write(bytes);
            in.close();
            out.close();
        }
    }


}
