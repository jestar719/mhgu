package cn.jestar.convert.skill;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Matcher;

import cn.jestar.convert.Compiler;
import cn.jestar.convert.bean.SkillBean;
import cn.jestar.convert.utils.RegexUtils;

import static cn.jestar.convert.index.UrlCompiler.END_FLAG;


/**
 * 抓取技能数据的Compiler.保存为对象
 * Created by 花京院 on 2019/1/31.
 */

public class SkillValueCompiler extends Compiler {

    public static final String START_FLAG = "<table class=\"t1\">";
    private final SkillBean mSkill;
    private LinkedList<SkillCompiler> mCompilers = new LinkedList<>();

    public SkillValueCompiler(String path) {
        super(path);
        mSkill = new SkillBean();
        mCompilers.add(new SkillEffectCompiler());
        mCompilers.add(new SkillJewelryCompiler());
        mCompilers.add(new SkillMaxValueCompiler());
    }

    @Override
    protected void onRead(BufferedReader reader) throws Exception {
        while (true) {
            String s = reader.readLine();
            if (s.contains(START_FLAG)) {
                SkillCompiler poll = mCompilers.poll();
                poll.compile(mSkill, reader);
                if (mCompilers.isEmpty())
                    return;
            }
        }
    }

    /**
     * 技能效果
     */
    public static class SkillEffectCompiler implements SkillCompiler {
        private static final String TR_START = "<tr>";
        private static final String TR_END = "</tr>";
        private static final String TD_START = "<td";
        private static final String TD_END = "</td>";
        private static final String SKILL_NAME_REGEX = "<td class=\"b\"><span style=\"color:\\w+;\">(.*)</span></td>";
        private static final String NAME_REGEX = "<td class=\"b\" rowspan=\"\\d\">(.*)</td>";
        private StringBuilder mBuilder;

        @Override
        public void compile(SkillBean bean, BufferedReader reader) throws IOException {
            LinkedList<String> strings = null;
            boolean flag = false;
            while (true) {
                String s = reader.readLine().trim();
                if (s.contains(END_FLAG)) {
                    return;
                }
                if (s.contains(TR_START)) {
                    strings = new LinkedList<>();
                } else if (s.contains(TR_END)) {
                    setSkill(bean, strings);
                } else {
                    if (s.contains(TD_END)) {
                        if (flag) {
                            mBuilder.append(s);
                            strings.add(mBuilder.toString());
                            flag = false;
                        } else {
                            strings.add(s);
                        }
                    }

                }
            }
        }

        private void setSkill(SkillBean bean, LinkedList<String> strings) {
            if (strings.size() > 3) {
                strings.poll();
                Matcher matcher = RegexUtils.getMatcher(strings.poll(), NAME_REGEX);
                if (matcher.find()) {
                    bean.setName(matcher.group(1));
                }
            }
            SkillBean.SkillEffect effect = new SkillBean.SkillEffect();
            String poll = strings.poll();
            effect.setName(getString(poll));
            poll = strings.poll();
            effect.setValue(Integer.parseInt(getString(poll)));

        }

        private String getString(String poll) {
            Matcher matcher = RegexUtils.getMatcher(poll, SKILL_NAME_REGEX);
            if (matcher.find()) {
                return matcher.group(1);
            }
            return poll;
        }
    }

    /**
     * 技能珠玉
     */
    public static class SkillJewelryCompiler implements SkillCompiler {
        @Override
        public void compile(SkillBean bean, BufferedReader reader) {

        }
    }

    /**
     * 技能最大值
     */
    public static class SkillMaxValueCompiler implements SkillCompiler {
        @Override
        public void compile(SkillBean bean, BufferedReader reader) {

        }
    }
}
