package cn.jestar.mhgu.equip;

import cn.jestar.db.bean.Skill;

/**
 * 技能数值统计
 * Created by 花京院 on 2019/10/5.
 */

public class SumSkill extends Skill {
    private int[] mSkillParts = new int[7];

    public SumSkill(Skill skill) {
        name = skill.getName();
        effectList = skill.getEffectList();
        id = skill.getId();
    }

    public int[] getSkillParts() {
        return mSkillParts;
    }

    public void reduce(int part, int value) {
        modify(part, value, false);
    }

    public void add(int part, int value) {
        modify(part, value, true);
    }

    private void modify(int part, int value, boolean isAdd) {
        if (value != 0) {
            value = (isAdd ? 1 : -1) * value;
            int i = mSkillParts[part];
            mSkillParts[part] = i + value;
            sum();
        }
    }

    private void sum() {
        value = 0;
        for (int i : mSkillParts) {
            value += i;
        }
    }
}
