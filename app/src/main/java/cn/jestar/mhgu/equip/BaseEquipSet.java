package cn.jestar.mhgu.equip;

/**
 * 配装数据基类
 * Created by 花京院 on 2019/10/15.
 */

public class BaseEquipSet {
    protected int maxDefend;
    protected int amuletSlot;
    protected int weaponSlot;
    protected String amuletSkill;
    protected String equipSkill;
    protected boolean isFight;
    private String description;

    public void setMaxDefend(int maxDefend) {
        this.maxDefend = maxDefend;
    }

    public void setAmuletSlot(int amuletSlot) {
        this.amuletSlot = amuletSlot;
    }

    public void setWeaponSlot(int weaponSlot) {
        this.weaponSlot = weaponSlot;
    }

    public void setAmuletSkill(String amuletSkill) {
        this.amuletSkill = amuletSkill;
    }

    public void setEquipSkill(String equipSkill) {
        this.equipSkill = equipSkill;
    }

    public void setFight(boolean fight) {
        isFight = fight;
    }

    public String getDescription() {
        if (description == null) {
            String temp = "%s %sS%s %sS武 防:%s\n%s";
            String str = isFight ? "近战" : "射手";
            description = String.format(temp, str, amuletSkill, amuletSlot, weaponSlot, maxDefend, equipSkill);
        }
        return description;
    }
}
