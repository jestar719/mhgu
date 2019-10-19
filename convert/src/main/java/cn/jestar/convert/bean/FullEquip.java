package cn.jestar.convert.bean;

import java.util.List;

/**
 * Created by 花京院 on 2019/9/25.
 */

public class FullEquip extends BaseEquip {
    private List<EquipSkill> skills;

    public List<EquipSkill> getSkills() {
        return skills;
    }

    public void setSkills(List<EquipSkill> skills) {
        this.skills = skills;
    }

    public BaseEquip getBaseEquip() {
        BaseEquip baseEquip = new BaseEquip();
        baseEquip.copy(this);
        return baseEquip;
    }
}
