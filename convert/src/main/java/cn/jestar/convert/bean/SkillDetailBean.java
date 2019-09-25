package cn.jestar.convert.bean;

/**
 * 技能详细
 * Created by 花京院 on 2019/8/31.
 */

public class SkillDetailBean {
    //无洞护石可发动的技能
    public static final int SINGLE = 0;
    //有洞护石可发动的技能
    public static final int SINGLE_WITH_STONE = 1;
    //仅护石不能发动的技能
    public static final int NEED_EQUIP = 2;
    //仅装备可发动的技能
    public static final int NO_SKILL = 3;
    //二名技能
    public static final int SPACIAL_SKILL = 4;
    private String name;
    private String jewelryName;
    private int jewelryNum;
    private int slotNum;
    private int slotValue;
    private int type;
    private String url;
    private int leftMaxValue;
    private int rightMaxValue;

    public int getLeftMaxValue() {
        return leftMaxValue;
    }

    public void setLeftMaxValue(int leftMaxValue) {
        this.leftMaxValue = leftMaxValue;
    }

    public int getRightMaxValue() {
        return rightMaxValue;
    }

    public void setRightMaxValue(int rightMaxValue) {
        this.rightMaxValue = rightMaxValue;
    }

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
        return Math.max(leftMaxValue,rightMaxValue);
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
        leftMaxValue = bean.getLeftMaxValue();
        rightMaxValue = bean.getRightMaxValue();
        int maxValue = getMaxValue();
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
        } else if (maxValue == 0) {
            type = NO_SKILL;
        } else {
            if (slotNum == 0) {
                type = SPACIAL_SKILL;
            } else {
                int value;
                if (slotNum==3){
                   value=slotValue;
                }else {
                    value= slotValue == 2 ? slotValue * 3 : slotValue + 1;
                }
                type = maxValue + value < 10 ? NEED_EQUIP : SINGLE_WITH_STONE;
            }

        }
    }

}
