package cn.jestar.mhgu.equip;

import java.util.List;

/**
 * 配装数据
 * Created by 花京院 on 2019/10/6.
 */

public class EquipSetValue {
    private List<SumSkill> mSumSkillList;
    private int[] mEquipValues;


    public List<SumSkill> getSumSkillList() {
        return mSumSkillList;
    }

    public EquipSetValue setSumSkillList(List<SumSkill> sumSkillList) {
        mSumSkillList = sumSkillList;
        return this;
    }

    public int[] getEquipValues() {
        return mEquipValues;
    }

    public EquipSetValue setEquipValues(int[] equipValues) {
        mEquipValues = equipValues;
        return this;
    }
}
