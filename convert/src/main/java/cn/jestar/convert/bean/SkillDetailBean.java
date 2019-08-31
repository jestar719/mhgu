package cn.jestar.convert.bean;

/**
 * Created by 花京院 on 2019/8/31.
 */

public class SkillDetailBean {
    public static final int SINGLE = 0;
    public static final int SINGLE_WITH_STONE = 1;
    public static final int NEED_EQUIP = 2;
    public static final int NO_SKILL = 3;
    private String name;
    private int maxValue;
    private String jewelryName;
    private int jewelryNum;
    private int slotNum;
    private int slotValue;
    private int type;
    private String url;

    public int getJewelryNum() {
        return jewelryNum;
    }

    public void setJewelryNum(int jewelryNum) {
        this.jewelryNum = jewelryNum;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public String getJewelryName() {
        return jewelryName;
    }

    public void setJewelryName(String jewelryName) {
        this.jewelryName = jewelryName;
    }

    public int getSlotNum() {
        return slotNum;
    }

    public void setSlotNum(int slotNum) {
        this.slotNum = slotNum;
    }

    public int getSlotValue() {
        return slotValue;
    }

    public void setSlotValue(int slotValue) {
        this.slotValue = slotValue;
    }

    public void setSkillBean(SkillBean bean, SkillJewelryBean jewelryBean, int jewelryNum) {
        name = bean.getName();
        maxValue = bean.getMaxValue();
        url = bean.getUrl();
        this.jewelryNum = jewelryNum;
        if (jewelryBean != null) {
            slotNum = jewelryBean.getSlotNum();
            slotValue = jewelryBean.getSkillValue();
            jewelryName = jewelryBean.getName();
        } else {
            slotNum = 0;
            slotValue = 0;
            jewelryName = null;
        }
        if (maxValue >= 10) {
            type = SINGLE;
        } else {
            if (slotValue + maxValue >= 10) {
                type = SINGLE_WITH_STONE;
            } else {
                if (slotNum == 2 && slotValue + maxValue + 1 > 10) {
                    type = SINGLE_WITH_STONE;
                } else if (slotValue == 2) {
                    type = maxValue + 6 >= 10 ? SINGLE_WITH_STONE : NEED_EQUIP;
                } else {
                    type = maxValue == 0 ? NO_SKILL : NEED_EQUIP;
                }
            }
        }
    }

}
