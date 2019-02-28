package cn.jestar.convert;

import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
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
import java.util.TreeMap;
import java.util.TreeSet;

import cn.jestar.convert.bean.TranslatedBean;
import cn.jestar.convert.utils.JsonUtils;

/**
 * 翻译的基类，
 * Created by 花京院 on 2019/2/12.
 */

public abstract class BaseConvertor extends Copyable {

    public String mJsonFileName;
    public String mTransBeanName;
    protected String mTemp = "%s.json";
    protected String mTransBeanTemp = "%sTransBean.json";
    protected String mName;
    protected File mTransBeanFile;
    protected CompileUrlAble mCompileUrlAble;

    public BaseConvertor setCompileUrlAble(CompileUrlAble compileUrlAble) {
        mCompileUrlAble = compileUrlAble;
        return this;
    }

    public void setName(String name) {
        mName = name;
        mJsonFileName = String.format(mTemp, mName);
        mTransBeanName = String.format(mTransBeanTemp, name);
    }


    public List<String> getLostList(List<String> orgin, List<String> handled) {
        Collections.sort(orgin);
        Collections.sort(handled);
        ArrayList<String> list = new ArrayList<>();
        int index = 0;
        int size = handled.size();
        for (String s : orgin) {
            if (index >= size) {
                list.add(s);
            } else {
                String s1 = handled.get(index);
                if (!s.equals(s1)) {
                    list.add(s);
                    index++;
                }
            }
        }
        return list;
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

    public Map<String, String> getMap(String name, File file) throws FileNotFoundException {
        FileReader reader = new FileReader(new File(file, name));
        java.lang.reflect.Type type = new TypeToken<TreeMap<String, String>>() {
        }.getType();
        return JsonUtils.fromStringByType(reader, type);
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
     * @param url      链接 格式为”data/xxxx.html" 或“ida/xxxx.html
     * @param transMap 文本对照Map
     * @param set      保存素材Url的Set
     * @throws Exception
     */
    public void translateFile(String url, Map<String, String> transMap, Set<String> set) throws Exception {
        StringBuilder builder = getText(url, set);
        translation(builder, transMap, getList(transMap));
        String s = builder.toString();
        write(new File(Constants.MH_PATH + url),s);
    }


    /**
     * 翻译
     *
     * @param url      链接 格式为”data/xxxx.html" 或“ida/xxxx.html
     * @param transMap 文本对照Map
     * @param list     日文文本
     * @param set      保存素材Url的Set
     * @throws Exception
     */
    public void translateFile(String url, Map<String, String> transMap, List<String> list, Set<String> set) throws Exception {
        StringBuilder builder = getText(url, set);
        translation(builder, transMap, list);
        String s = builder.toString();
        write(new File(Constants.MH_PATH + url),s);
    }

    /**
     * 读取文件，保存内容到StringBuilder ,并收集相关的url
     * @param url
     * @param set
     * @return
     * @throws Exception
     */
    public StringBuilder getText(String url, Set<String> set) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(Constants.MH_PATH + url));
        StringBuilder builder = new StringBuilder();
        String text;
        String separator = System.lineSeparator();
        boolean needCompile = mCompileUrlAble != null && mCompileUrlAble.needCompile(url);
        boolean canCompile = needCompile && set != null;
        while ((text = reader.readLine()) != null) {
            text = text.trim();
            if (!text.isEmpty()) {
                builder.append(text).append(separator);
                if (canCompile) {
                    mCompileUrlAble.compile(text, set);
                }
            }
        }
        reader.close();
        return builder;
    }


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

    public interface CompileUrlAble {
        boolean needCompile(String url);

        void compile(String text, Set<String> set);
    }
}
