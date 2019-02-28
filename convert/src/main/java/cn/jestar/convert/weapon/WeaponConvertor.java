package cn.jestar.convert.weapon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import cn.jestar.convert.BaseConvertor;
import cn.jestar.convert.Constants;
import cn.jestar.convert.bean.TranslatedBean;
import cn.jestar.convert.utils.JsonUtils;
import cn.jestar.convert.utils.RegexUtils;

/**
 * 用与武器翻译的类
 * Created by 花京院 on 2019/2/4.
 */

public class WeaponConvertor extends BaseConvertor {
    public static final String REGEX = "<a href=\"../(ida/\\d+\\.html)\">.*</a> x\\d+<br>";
    public static final String REGEX1 = "<span style=\"background-color:#.*;\">入手端材：<a href=\"../(ida/\\d+\\.html)\">.*</a> x\\d+</span><br>";
    private final File mSummaryFile;
    private final File mTranslatedFile;
    private final File mTranslationFile;
    private String mWeapon = "weapon";


    public WeaponConvertor(String name) {
        setName(name);
        mSummaryFile = new File(Constants.TEMP_SUMMARY_PATH, mWeapon);
        mTranslatedFile = new File(Constants.TEMP_TRANSLATED_PATH, mWeapon);
        mTranslationFile = new File(Constants.TEMP_TRANSLATION_PATH, mWeapon);
        mTransBeanFile = new File(mTranslatedFile, mTransBeanName);
        mCompileUrlAble = new CompileUrlAble() {
            @Override
            public boolean needCompile(String url) {
                return url.contains("ida");
            }

            @Override
            public void compile(String text, Set<String> set) {
                if (text.matches(REGEX)) {
                    set.add(RegexUtils.getMatchText(text, REGEX));
                } else if (text.matches(REGEX1)) {
                    set.add(RegexUtils.getMatchText(text, REGEX1));
                }
            }
        };
    }

    /**
     * 根据名字创建对应的TranslatedBean{@link TranslatedBean}
     *
     * @param list 该类相关的url列表
     * @throws Exception
     */
    public void makeBean(List<String> list) throws Exception {
        Map<String, String> map = getMap(mJsonFileName, mSummaryFile);
        TranslatedBean bean = new TranslatedBean();
        TreeSet<String> strings = new TreeSet<>(list);
        strings.addAll(map.values());
        bean.setUrls(new ArrayList<>(strings));
        map = getMap();
        bean.setTexts(map);
        FileWriter writer = new FileWriter(mTransBeanFile);
        writer.write(JsonUtils.toString(bean));
        writer.close();
    }

    public Map<String, String> getMap() throws FileNotFoundException {
        return getMap(mJsonFileName, mTranslatedFile);
    }

    /**
     * 比较Translation和Translated中同名的文件，获取Translated中非翻译的名字集合
     *
     * @return Translated中没有的名字集合
     * @throws Exception
     */
    public List<String> getNotTranslatedNames() throws Exception {
        File source = new File(mTranslationFile, mJsonFileName);
        List<String> list = JsonUtils.toList(new FileReader(source), String.class);
        TreeMap<String, String> treeMap = JsonUtils.fromString(new FileReader(new File(mTranslatedFile, mJsonFileName)), TreeMap.class);
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
     *
     * @return translation中漏掉的名字集合
     * @throws Exception
     */
    public List<String> getLostNamesInTranslation() throws Exception {
        String name = mJsonFileName;
        File source = new File(mTranslationFile, name);
        List<String> list = JsonUtils.toList(new FileReader(source), String.class);
        TreeMap<String, String> treeMap = JsonUtils.fromString(new FileReader(new File(mTranslatedFile, name)), TreeMap.class);
        ArrayList<String> list1 = new ArrayList<>();
        for (String s : treeMap.keySet()) {
            if (list.indexOf(s) == -1) {
                list1.add(s);
            }
        }
        return list1;
    }


}
