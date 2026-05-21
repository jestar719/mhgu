package cn.jestar.db.bean;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

/**
 * 装备技能
 * Created by 花京院 on 2019/9/19.
 */
@Entity
public class EquipSkill extends BaseSkill {
    @ColumnInfo
    private int equipId;

    public EquipSkill() {
    }

    @Ignore
    public EquipSkill(String name, int value) {
        super(name, value);
    }

    @Ignore
    public EquipSkill(BaseSkill skill) {
        super(skill.name, skill.value);
    }

    public int getEquipId() {
        return equipId;
    }

    public void setEquipId(int equipId) {
        this.equipId = equipId;
    }
}
