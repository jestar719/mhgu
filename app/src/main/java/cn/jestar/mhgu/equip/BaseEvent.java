package cn.jestar.mhgu.equip;

import cn.jestar.db.bean.Jewelry;

/**
 * Created by 花京院 on 2019/10/5.
 */

public class BaseEvent {
    protected int type;
    protected Jewelry mJewelry;

    public BaseEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Jewelry getJewelry() {
        return mJewelry;
    }

    public void setJewelry(Jewelry jewelry) {
        mJewelry = jewelry;
    }

    public static @interface Type {
        int EQUIP = 0;
        int JEWELRY = 1;
        int SKILL = 2;
    }
}
