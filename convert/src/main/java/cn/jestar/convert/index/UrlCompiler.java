package cn.jestar.convert.index;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;

import cn.jestar.convert.Compiler;
import cn.jestar.convert.bean.LinkInfo;
import cn.jestar.convert.utils.RegexUtils;

/**
 * 用与解析含有{@link #LINK_REGEX}文本的Html的类。
 * Created by 花京院 on 2019/1/30.
 */

public abstract class UrlCompiler extends Compiler {
    public static final String START_FLAG = "<table class=";
    public static final String END_FLAG = "</table>";
    public static final String NAME_FLAG = "<th class=\"th1\"";
    public static final String NAME_END = "</th>";
    public static final String NAME_REGEX = "<th class=\"th1\" colspan=\"\\d+\">(.*)</th>";
    public static final String LINK_REGEX = "<a href=\"../(.*)\">(.*)</a>";
    public static final String TD = "<td";
    public static final String SPLIT = " / ";


    public UrlCompiler(String path) {
        super(path);
    }

    @Override
    protected void onRead(BufferedReader reader) throws IOException {
        String text;
        boolean flag = true;
        while (flag && (text = reader.readLine()) != null) {
            flag = onRead(reader, text);
        }
    }

    /**
     * 逐行处理
     *
     * @param reader
     * @param text   当前一行的文本
     * @return true表示继续，false表示结束
     */
    protected boolean onRead(BufferedReader reader, String text) {
        return true;
    }

    protected void setLink(String text, LinkInfo linkInfo) {
        Matcher matcher = RegexUtils.getMatcher(text, LINK_REGEX);
        if (matcher.find()) {
            linkInfo.setData(matcher.group(2), matcher.group(1));
        }
    }
}
