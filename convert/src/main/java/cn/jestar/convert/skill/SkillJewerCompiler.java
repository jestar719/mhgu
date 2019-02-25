package cn.jestar.convert.skill;

import java.io.BufferedReader;

import cn.jestar.convert.index.UrlCompiler;

/**
 * Created by 花京院 on 2019/2/20.
 */

public class SkillJewerCompiler extends UrlCompiler {
    public SkillJewerCompiler(String path) {
        super(path);
    }

    @Override
    protected boolean onRead(BufferedReader reader, String text) {

        return true;
    }
}
