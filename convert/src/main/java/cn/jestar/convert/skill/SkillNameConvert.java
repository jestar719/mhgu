package cn.jestar.convert.skill;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Set;

import cn.jestar.convert.BaseConvertor;
import cn.jestar.convert.Constants;

/**
 * Created by 花京院 on 2019/2/20.
 */

public class SkillNameConvert extends BaseConvertor {
    public SkillNameConvert(String name) {
        setName(name);
    }

    @Override
    public StringBuilder getText(String url, Set<String> set) throws Exception {
        StringBuilder builder = new StringBuilder();
        String separator = System.lineSeparator();
        File file = new File(Constants.MH_PATH, url);

        try (FileReader reader = new FileReader(file);
             BufferedReader br = new BufferedReader(reader)) {
            String text;
            boolean needGetLink = url.contains("ida");
            while ((text = br.readLine()) != null) {
                text = text.trim();
                if (text.length() > 0) {
                    builder.append(text).append(separator);
                    if (set != null && needGetLink) {
                        getLink(text, set);
                    }
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return builder;
    }

    protected void getLink(String text, Set<String> set) {

    }
}
