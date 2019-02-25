package cn.jestar.convert.skill;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import cn.jestar.convert.bean.SkillBean;
import cn.jestar.convert.index.UrlCompiler;
import cn.jestar.convert.utils.RegexUtils;

/**
 * Created by 花京院 on 2019/2/7.
 */

public class SkillEffectCompiler extends UrlCompiler {
    public static final String TR_START = "<tr";
    public static final String TD_CLASS = "<td class";
    public static final String SKILL_NAME_REGEX = "<td class=\"\\w+\"><span style=\"color:\\w+;\">(.*)</span></td>";
    public static final String LINK_START = "<a href";
    private List<SkillBean> mList;
    private SkillBean mBean;

    public SkillEffectCompiler(String path) {
        super(path);
        mList = new ArrayList<>();
    }

    @Override
    protected void onRead(BufferedReader reader) throws IOException {
        String text;
        boolean tableStart = false;
        while ((text = reader.readLine()) != null) {
            if (tableStart) {
                if (text.contains(END_FLAG)) {
                    return;
                } else if (text.contains(TR_START)) {
                    readSkillOrEffect(reader);
                }
            } else {
                tableStart = isTableStart(text);
            }
        }
    }

    /**
     * 返回解析的数据集合
     *
     * @return
     */
    public List<SkillBean> getList() {
        readFile();
        return mList;
    }

    /**
     * 解析技能或是技能效果
     *
     * @param reader
     */
    private void readSkillOrEffect(BufferedReader reader) throws IOException {
        String s = reader.readLine();
        SkillBean.SkillEffect effect;
        if (s.contains(TD_CLASS)) {
            effect = new SkillBean.SkillEffect();
            effect.setName(getText(s));
            effect.setValue(getValue(reader.readLine()));
            effect.setDescription(getText(reader.readLine()));
            mBean.addSkillEffect(effect);
        } else if (s.contains(LINK_START)) {
            mBean = new SkillBean();
            mList.add(mBean);
            Matcher matcher = RegexUtils.getMatcher(s, LINK_REGEX);
            if (matcher.find()) {
                mBean.setUrl(matcher.group(1));
                mBean.setName(matcher.group(2));
            }
            readSkillOrEffect(reader);
        }else {
            readSkillOrEffect(reader);
        }
    }

    /**
     * 获取技能效果的数值
     *
     * @param s
     * @return
     */
    public int getValue(String s) {
        String text = getText(s);
        return Integer.parseInt(text);
    }

    /**
     * 获取技能效果名字或数值
     *
     * @param s
     * @return
     */
    public String getText(String s) {
        return RegexUtils.getMatchText(s, SKILL_NAME_REGEX);
    }

    /**
     * 是否读取到表格
     *
     * @param text 当前行
     * @return
     */
    private boolean isTableStart(String text) {
        return text.contains(START_FLAG);
    }
}
