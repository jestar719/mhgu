package cn.jestar.convert.weapon;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import cn.jestar.convert.utils.RegexUtils;

import static cn.jestar.convert.index.UrlCompiler.END_FLAG;

/**
 * Created by 花京院 on 2019/2/9.
 */

public class WeaponNameCompiler extends WeaponCompiler {
    private List<String> mList;
    private String mLastName;

    @Override
    protected void onRead(BufferedReader reader) throws Exception {
        boolean flag = true;
        String name = null;
        boolean startFlag = false;
        while (flag) {
            String s = reader.readLine().trim();
            if (startFlag) {
                if (s.matches(NAME_REGEX)) {
                    Matcher matcher = RegexUtils.getMatcher(s, NAME_REGEX);
                    if (matcher.find()) {
                        name = matcher.group(2);
                    }
                } else if (s.length() > 0 && !s.contains(">")) {
                    name = RegexUtils.getTextWithoutNum(s);
                } else if (s.contains(TD_END)) {
                    if (!name.equals(mLastName)) {
                        mLastName = name;
                        mList.add(name);
                    }
                    startFlag = false;
                } else if (s.contains(END_FLAG)) {
                    flag = false;
                }
            } else {
                if (s.matches(START_REGEX)) {
                    startFlag = true;
                }
            }
        }
    }

   public List<String> getNames(File file){
        setFile(file);
        mList=new ArrayList<>();
        readFile();
        return mList;
   }
}
