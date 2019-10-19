package cn.jestar.mhgu.equip;

import cn.jestar.db.bean.BaseEquip;
import cn.jestar.db.bean.Skill;

/**
 * 菜单选择事件
 * Created by 花京院 on 2019/10/5.
 */

public class MenuSelectEvent extends BaseEvent {
    private BaseEquip mEquip;
    private Skill mSkill;

    public MenuSelectEvent(int type) {
        super(type);
    }

    public BaseEquip getEquip() {
        return mEquip;
    }

    public void setEquip(BaseEquip equip) {
        mEquip = equip;
    }

    public Skill getSkill() {
        return mSkill;
    }

    public void setSkill(Skill skill) {
        mSkill = skill;
    }
}
