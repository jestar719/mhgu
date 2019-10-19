package cn.jestar.db.bean;

import android.arch.persistence.room.Entity;

/**
 * 装饰品
 * Created by 花京院 on 2019/9/19.
 */

@Entity
public class Jewelry extends BaseSkill {
    private int slotNum;
    private int debuffValue;
    private String debuff;
    private String skillName;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDebuffValue() {
        return debuffValue;
    }

    public void setDebuffValue(int debuffValue) {
        this.debuffValue = debuffValue;
    }

    public String getDebuff() {
        return debuff;
    }

    public void setDebuff(String debuff) {
        this.debuff = debuff;
    }

    public int getSlotNum() {
        return slotNum;
    }

    public void setSlotNum(int slotNum) {
        this.slotNum = slotNum;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }
}
