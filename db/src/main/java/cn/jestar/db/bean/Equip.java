package cn.jestar.db.bean;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import java.util.List;

/**
 * 完整的装备数据
 * Created by 花京院 on 2019/10/3.
 */
@Entity
public class Equip extends BaseEquip {
    @Ignore
    private List<EquipSkill> skills;

    public List<EquipSkill> getSkills() {
        return skills;
    }

    public void setSkills(List<EquipSkill> skills) {
        this.skills = skills;
    }

}
