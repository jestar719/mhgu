package cn.jestar.convert.collect;

import java.io.BufferedReader;
import java.io.IOException;

import cn.jestar.convert.bean.SkillBean;

/**
 * Created by 花京院 on 2019/1/31.
 */

public interface SkillCompiler {
    void compile(SkillBean bean, BufferedReader reader) throws IOException;
}
