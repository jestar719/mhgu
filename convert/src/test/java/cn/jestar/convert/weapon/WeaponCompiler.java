package cn.jestar.convert.weapon;

import java.io.BufferedReader;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;

import cn.jestar.convert.Compiler;
import cn.jestar.convert.utils.RegexUtils;

import static cn.jestar.convert.index.UrlCompiler.END_FLAG;

/**
 * Created by 花京院 on 2019/2/2.
 */

public class WeaponCompiler extends Compiler {
    public static final String START_REGEX = ".*<tr class=\"(.*?)plb.*?";
    public static final String NAME_REGEX = ".*<a href=\"../(ida/\\d+\\.html)\">(\\D+)([\\d]{0,2})</a>";
    private static final String TD_END = "</td>";

    private Map<String, String> mMap = new TreeMap<>();

    public WeaponCompiler() {
    }

    public WeaponCompiler(String path) {
        super(path);
    }

    public WeaponCompiler(File file) {
        super(file);
    }

    @Override
    public void setFile(String path) {
        super.setFile(path);
        mMap = new TreeMap<>();
    }

    @Override
    public void setFile(File file) {
        super.setFile(file);
        mMap = new TreeMap<>();
    }

    @Override
    protected void onRead(BufferedReader reader) throws Exception {
        boolean flag = true;
        String url = null;
        String name = null;
        boolean startFlag = false;
        while (flag) {
            String s = reader.readLine().trim();
            if (startFlag) {
                if (s.matches(NAME_REGEX)) {
                    Matcher matcher = RegexUtils.getMatcher(s, NAME_REGEX);
                    if (matcher.find()) {
                        url = matcher.group(1);
                        name = matcher.group(2);
                    }
                } else if (s.length() > 0 && !s.contains(">")) {
                    name = RegexUtils.getTextWithoutNum(s);
                } else if (s.contains(TD_END)) {
                    mMap.put(name, url);
                    url = null;
                    name = null;
                    startFlag = false;
                } else if (s.contains(END_FLAG)) {
                    flag = false;
                }
            } else {
                if (s.matches(START_REGEX)) {
                    url = getUrl(s);
                    startFlag = true;
                }
            }
        }
    }

    public Map<String, String> getMap() {
        return mMap;
    }

    private String getUrl(String s) {
        if (s != null) {
            s = RegexUtils.getMatchText(s, START_REGEX);
            if (s != null) {
                s = RegexUtils.getMatchNum(s);
                if (s != null) {
                    s = String.format("ida/%s.html", s);
                }
            }
        }
        return s;
    }
}
