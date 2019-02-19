package cn.jestar.convert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import cn.jestar.convert.bean.TranslatedBean;
import cn.jestar.convert.utils.JsonUtils;

/**
 * Created by 花京院 on 2019/2/12.
 */

public abstract class BaseConvertor {

    protected String mTemp = "%s.json";
    protected String mTransBeanTemp = "%sTransBean.json";
    protected String mName;
    protected File mTransBeanFile;
    protected String mJsonFileName;
    protected String mTransBeanName;

    public void setName(String name) {
        mName = name;
        mJsonFileName = String.format(mTemp, mName);
        mTransBeanName=String.format(mTransBeanTemp,name);
    }

    /**
     * 读取相关的TranslatedBean翻译,根据其中的Url，读取并翻译。
     *
     * @throws Exception
     */
    public void translation() throws Exception {
        TranslatedBean bean = getBean();
        Map<String, String> texts = bean.getTexts();
        List<String> urls = bean.getUrls();
        List<String> list = getList(texts);
        TreeSet<String> set = new TreeSet<>();
        for (String url : urls) {
            translateFile(url, texts, list, set);
        }
        for (String url : set) {
            translateFile(url, texts, list, null);
        }
    }

    /**
     * 读取TranslatedBean {@link TranslatedBean}
     *
     * @return TranslatedBean
     * @throws FileNotFoundException
     */
    public TranslatedBean getBean() throws Exception {
        return JsonUtils.fromString((new FileReader(mTransBeanFile)), TranslatedBean.class);
    }

    /**
     * 翻译
     *
     * @param url      链接
     * @param transMap 文本对照Map
     * @param list     日文文本
     * @param set      保存素材Url的Set
     * @throws Exception
     */
    public void translateFile(String url, Map<String, String> transMap, List<String> list, Set<String> set) throws Exception {
        StringBuilder builder = getText(url, set);
        translation(builder, transMap, list);
        String s = builder.toString();
        write(s, url);
    }

    protected abstract StringBuilder getText(String url, Set<String> set) throws Exception;

    /**
     * 替换文本
     *
     * @param builder
     * @param texts
     * @param s
     */
    public void replaceText(StringBuilder builder, Map<String, String> texts, String s) {
        int start;
        int index = 0;
        int length = s.length();
        String value = texts.get(s);
        int vLength = value.length();
        while ((start = builder.indexOf(s, index)) != -1) {
            int end = start + length;
            builder.replace(start, end, value);
            index = start + vLength;
        }
    }

    /**
     * 写入文本
     *
     * @param string 文本
     * @param url    写入的url
     * @throws IOException
     */
    public void write(String string, String url) throws IOException {
        FileWriter writer = new FileWriter(Constants.MH_PATH + url);
        writer.write(string);
        writer.close();
    }

    /**
     * 翻译
     *
     * @param builder 读取的文本数据{@link StringBuilder}
     * @param texts   文本对照表
     * @param list    日文文本集合
     */
    public void translation(StringBuilder builder, Map<String, String> texts, List<String> list) {
        for (String s : list) {
            replaceText(builder, texts, s);
        }
    }

    /**
     * 获取反序后的List
     *
     * @param texts 文本对照表
     * @return 反序排列的日文的文本
     */
    public List<String> getList(Map<String, String> texts) {
        List<String> list = new ArrayList<>(texts.keySet());
        //这里反序排列，以防aabbcc 被 aabb先替换掉
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2) * -1;
            }
        });
        return list;
    }
}
