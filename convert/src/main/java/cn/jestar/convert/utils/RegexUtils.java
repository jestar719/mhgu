package cn.jestar.convert.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 花京院 on 2019/1/29.
 */

public class RegexUtils {
    public final static String END_NUM = "(\\D*)\\d+$";
    public static final String NUM_REGEX = "\\d+";

    public static String getTextWithoutNum(String text) {
        String result = text;
        Matcher matcher = Pattern.compile(END_NUM).matcher(text);
        if (matcher.find()) {
            result = matcher.group(1);
        }
        return result;
    }

    /**
     * 根据文本及匹配规则获取匹配器
     *
     * @param text  文本
     * @param regex 匹配规则
     * @return 匹配器
     */
    public static Matcher getMatcher(String text, String regex) {
        return Pattern.compile(regex).matcher(text);
    }

    /**
     * 根据规则提取文本中匹配的文本
     *
     * @param text  文本
     * @param regex 匹配规则
     * @return 提取的匹配的文本
     */
    public static String getMatchText(String text, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    /**
     * 获取文本中的第一个数字
     *
     * @param text 文本
     * @return 匹配的数字
     */
    public static String getMatchNum(String text) {
        Matcher matcher = getMatcher(text, NUM_REGEX);
        String s = null;
        if (matcher.find()) {
            s = matcher.group();
        }
        return s;
    }
}
