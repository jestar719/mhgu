package cn.jestar.convert.weapon;

import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import cn.jestar.convert.Constants;
import cn.jestar.convert.bean.TranslatedBean;
import cn.jestar.convert.utils.JsonUtils;
import cn.jestar.convert.utils.RegexUtils;

/**
 * Created by 花京院 on 2019/2/4.
 */

public class WeaponConvertor {
    public static final String REGEX = "<a href=\"../(ida/\\d+\\.html)\">.*</a> x\\d+<br>";
    public static final String REGEX1 = "<span style=\"background-color:#.*;\">入手端材：<a href=\"../(ida/\\d+\\.html)\">.*</a> x\\d+</span><br>";
    private final File mSummaryFile;
    private final File mTranslatedFile;
    private final File mTranslationFile;
    private String mWeapon = "weapon";
    private String mTemp = "%s.json";
    private String mTransBeaTemp = "%sTransBean.json";
    private String mName;

    public WeaponConvertor(String name) {
        mName = name;
        mSummaryFile = new File(Constants.TEMP_SUMMARY_PATH, mWeapon);
        mTranslatedFile = new File(Constants.TEMP_TRANSLATED_PATH, mWeapon);
        mTranslationFile = new File(Constants.TEMP_TRANSLATION_PATH, mWeapon);
    }


    public void makeBean(List<String> list) throws Exception {
        String name = getName();
        Map<String, String> map = getMap(name, mSummaryFile);
        TranslatedBean bean = new TranslatedBean();
        TreeSet<String> strings = new TreeSet<>(list);
        strings.addAll(map.values());
        bean.setUrls(new ArrayList<>(strings));
        map = getMap(name, mTranslatedFile);
        bean.setTexts(map);
        FileWriter writer = new FileWriter(new File(mTranslatedFile, String.format(mTransBeaTemp, mName)));
        writer.write(JsonUtils.toString(bean));
        writer.close();
    }

    private String getName() {
        return String.format(mTemp, mName);
    }

    private Map<String, String> getMap(String name, File file) throws FileNotFoundException {
        FileReader reader = new FileReader(new File(file, name));
        Type type = new TypeToken<TreeMap<String, String>>() {
        }.getType();
        return JsonUtils.fromStringByType(reader, type);
    }

    /**
     * 比较Translation和Translated中同名的文件，获取Translated中非翻译的名字集合
     * @return Translated中没有的名字集合
     * @throws Exception
     */
    public List<String> getNotTranslatedNames() throws Exception {
        String name = getName();
        File source = new File(mTranslationFile, name);
        List<String> list = JsonUtils.toList(new FileReader(source), String.class);
        TreeMap<String, String> treeMap = JsonUtils.fromString(new FileReader(new File(mTranslatedFile, name)), TreeMap.class);
        ArrayList<String> list1 = new ArrayList<>();
        for (String s : list) {
            String s1 = treeMap.get(s);
            if (s1 == null || s1.isEmpty()) {
                list1.add(s);
            }
        }
        return list1;
    }

    /**
     * 比较Translation和Translated中同名的文件，获取Translation中漏掉的名字
     * @return ranslation中漏掉的名字集合
     * @throws Exception
     */
    public List<String> getLostNamesInTranslation() throws Exception {
        String name = getName();
        File source = new File(mTranslationFile, name);
        List<String> list = JsonUtils.toList(new FileReader(source), String.class);
        TreeMap<String, String> treeMap = JsonUtils.fromString(new FileReader(new File(mTranslatedFile, name)), TreeMap.class);
        ArrayList<String> list1 = new ArrayList<>();
        for (String s : treeMap.keySet()) {
            if (list.indexOf(s)==-1){
                list1.add(s);
            }
        }
        return list1;
    }



    /**
     * 读取相关的TranslatedBean翻译,根据其中的Url，读取并翻译。
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
     * 翻译
     * @param url 链接
     * @param transMap 文本对照Map
     * @param JTextlist 日文文本
     * @param set 保存素材Url的Set
     * @throws Exception
     */
    public void translateFile(String url, Map<String, String> transMap, List<String> JTextlist, TreeSet<String> set) throws Exception {
        StringBuilder builder = getText(url, set);
        translation(builder, transMap, JTextlist);
        String s = builder.toString();
        write(s, url);
    }

    /**
     * 解析相关素材的Url并保存
     * @param text 一行文本
     * @param links 保存url的Set
     */
    public void getUrls(String text, Set<String> links) {
        if (text.matches(REGEX)) {
            links.add(RegexUtils.getMatchText(text, REGEX));
        } else if (text.matches(REGEX1)) {
            links.add(RegexUtils.getMatchText(text, REGEX1));
        }
    }

    /**
     * 获取反序后的List
     * @param texts
     * @return
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

    /**
     * 创建TraslatedBean {@link TranslatedBean}
     * @return TranslatedBean
     * @throws FileNotFoundException
     */
    public TranslatedBean getBean() throws FileNotFoundException {
        String name = String.format(mTransBeaTemp, mName);
        File file = new File(mTranslatedFile, name);
        return JsonUtils.fromString((new FileReader(file)), TranslatedBean.class);
    }

    public void translation(StringBuilder builder, Map<String, String> texts, List<String> list) {
        for (String s : list) {
            replaceText(builder, texts, s);
        }
    }

    /**
     * 替换文本
     * @param builder
     * @param texts
     * @param s
     */
    public void replaceText(StringBuilder builder, Map<String, String> texts, String s) {
        int start;
        int index = 0;
        int length = s.length();
        String value = texts.get(s);
        while ((start = builder.indexOf(s, index)) != -1) {
            int end = start + length;
            builder.replace(start, end, value);
            index = start;
        }
    }

    /**
     * 读取文件，保存在StringBuilder中，并解析相关素材的url并保存
     * @param url 文件的url
     * @param links 用与收集素材的url的Set
     * @return 文件的String
     * @throws Exception
     */
    public StringBuilder getText(String url, Set<String> links) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(Constants.MH_PATH + url));
        StringBuilder builder = new StringBuilder();
        String text;
        String separator = System.lineSeparator();
        boolean isIda = url.contains("ida");
        while ((text = reader.readLine()) != null) {
            text = text.trim();
            if (!text.isEmpty()) {
                builder.append(text).append(separator);
                if (isIda && links != null) {
                    getUrls(text, links);
                }
            }
        }
        reader.close();
        return builder;
    }

    /**
     * 写入文本
     * @param string 文本
     * @param url 写入的url
     * @throws IOException
     */
    public void write(String string, String url) throws IOException {
        FileWriter writer = new FileWriter(Constants.MH_PATH + url);
        writer.write(string);
        writer.close();
    }
}
