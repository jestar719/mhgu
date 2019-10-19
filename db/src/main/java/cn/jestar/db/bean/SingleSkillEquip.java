package cn.jestar.db.bean;

import android.arch.persistence.room.Entity;

/**
 * 单技能显示的装备数据
 * Created by 花京院 on 2019/9/19.
 */

@Entity
public class SingleSkillEquip extends BaseEquip {
    private String skillName;
    private int skillValue;

    public SingleSkillEquip() {
    }

    public SingleSkillEquip(BaseEquip equip, BaseSkill skill) {
        copy(equip);
        skillName = skill.getName();
        skillValue = skill.getValue();
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public int getSkillValue() {
        return skillValue;
    }

    public void setSkillValue(int skillValue) {
        this.skillValue = skillValue;
    }

    public EquipSkill getSkill() {
        EquipSkill skill = new EquipSkill(skillName, skillValue);
        skill.setEquipId(id);
        return skill;
    }


}
