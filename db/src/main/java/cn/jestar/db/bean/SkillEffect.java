package cn.jestar.db.bean;

import android.arch.persistence.room.Entity;

/**
 * 技能效果
 * Created by 花京院 on 2019/9/19.
 */

@Entity
public class SkillEffect extends BaseSkill {
    private String skillName;
    private String effect;

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public void Effect(String effect) {
        this.effect = effect;
    }
}
