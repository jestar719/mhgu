package cn.jestar.convert.bean;

import cn.jestar.convert.utils.RegexUtils;

/**
 * Created by 花京院 on 2019/8/31.
 */

public class SkillJewelryBean implements Comparable<SkillJewelryBean> {
    private String name;
    private String skill;
    private String debuff;
    private int skillValue;
    private int debuffValue;
    private int slotNum;
    private String mUrl;


    public SkillJewelryBean(String text) {
        name = text;
        slotNum = getInt(text);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getDebuff() {
        return debuff;
    }

    public void setDebuff(String debuff) {
        this.debuff = debuff;
    }

    public int getSkillValue() {
        return skillValue;
    }

    public void setSkillValue(int skillValue) {
        this.skillValue = skillValue;
    }

    public int getDebuffValue() {
        return debuffValue;
    }

    public void setDebuffValue(int debuffValue) {
        this.debuffValue = debuffValue;
    }

    public int getSlotNum() {
        return slotNum;
    }

    public void setSlotNum(int slotNum) {
        this.slotNum = slotNum;
    }

    public int getInt(String text) {
        int value = 0;
        try {
            value = Integer.parseInt(RegexUtils.getMatchNum(text));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return value;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    @Override
    public int compareTo(SkillJewelryBean o) {
        int result =o==null?-1:skill.compareTo(o.skill);
        return result == 0 ? skillValue - o.getSkillValue() : result;
    }
}
