package cn.jestar.mhgu.equip;

import java.util.List;

import cn.jestar.db.bean.BaseSkill;

/**
 * 配装记录
 * Created by 花京院 on 2019/10/5.
 */

public class EquipSetRecode extends BaseEquipSet {
    private int[] ids;
    private List<int[]> jewelryIds;
    private List<BaseSkill> skillValues;

    public EquipSetRecode() {
    }

    public int[] getIds() {
        return ids;
    }

    public void setIds(int[] ids) {
        this.ids = ids;
    }

    public List<int[]> getJewelryIds() {
        return jewelryIds;
    }

    public void setJewelryIds(List<int[]> jewelryIds) {
        this.jewelryIds = jewelryIds;
    }

    public List<BaseSkill> getSkillValues() {
        return skillValues;
    }

    public void setSkillValues(List<BaseSkill> skillValues) {
        this.skillValues = skillValues;
    }

}
