package cn.jestar.mhgu.equip;

import cn.jestar.db.bean.BaseSkill;

/**
 * 点击状态
 * Created by 花京院 on 2019/10/5.
 */

public class SelectEvent extends BaseEvent {

    private boolean remove;
    private int part;
    private int mIndex;
    private BaseSkill mSkill;

    public SelectEvent(int part, boolean remove, int type) {
        super(type);
        this.part = part;
        this.remove = remove;
    }

    public int getPart() {
        return part;
    }

    public void setPart(int part) {
        this.part = part;
    }

    public boolean isRemove() {
        return remove;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public BaseSkill getSkill() {
        return mSkill;
    }

    public void setSkill(BaseSkill skill) {
        mSkill = skill;
    }
}
