package cn.jestar.convert.collect;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import cn.jestar.convert.Constans;
import cn.jestar.convert.index.UrlCompiler;
import cn.jestar.convert.utils.RegexUtils;

/**
 * Created by 花京院 on 2019/1/31.
 */

public class SkillCollectCompiler extends UrlCompiler {
    private Map<String, String> mMap = new HashMap<>();

    public SkillCollectCompiler() {
        super(Constans.DATA_PATH + "2200.html");
    }

    @Override
    protected void onRead(BufferedReader reader) throws IOException {
        boolean flag = false;
        while (true) {
            String s = reader.readLine();
            if (s.contains(START_FLAG)) {
                flag = true;
            } else if (s.contains(END_FLAG)) {
                File file = new File(Constans.TEMP_PATH);
                write(mMap, new File(file, "技能一览.json"));
                return;
            } else {
                if (flag) {
                    Matcher matcher = RegexUtils.getMatcher(s, LINK_REGEX);
                    if (matcher.find()) {
                        mMap.put(matcher.group(2), matcher.group(1));
                    }
                }
            }
        }
    }
}
