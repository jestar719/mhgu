package cn.jestar.convert.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 花京院 on 2019/1/31.
 */

public class SkillBean {
    private int id;
    private String name;
    @SerializedName("effect")
    private String description;
    private List<SkillEffect> effectList = new ArrayList<>();
    private List<SkillJewelryBean> jewelryList =null;
    private int leftMaxValue;
    private int rightMaxValue;
    private String url;
    private int type;

    public int getType() {
        return type;
    }

    public void setMaxValues(int[] max) {
        int i = max.length - 1;
        for (int i1 = 0; i1 < i; i1++) {
            int maxValue = max[i1];
            boolean isLeft = i1 % 2 == 0;
            int value = isLeft ? leftMaxValue : rightMaxValue;
            if (maxValue > value) {
                if (isLeft) {
                    leftMaxValue = maxValue;
                } else {
                    rightMaxValue = maxValue;
                }
            }
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public SkillBean setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public SkillBean setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public SkillBean setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<SkillJewelryBean> getJewelryList() {
        return jewelryList;
    }

    public List<SkillEffect> getEffectList() {
        return effectList;
    }

    public SkillBean setEffectList(List<SkillEffect> effectList) {
        this.effectList = effectList;
        return this;
    }

    public SkillBean addSkillEffect(SkillEffect effect) {
        effectList.add(effect);
        return this;
    }

    public int getLeftMaxValue() {
        return leftMaxValue;
    }

    public SkillBean setLeftMaxValue(int leftMaxValue) {
        this.leftMaxValue = leftMaxValue;
        return this;
    }

    public int getRightMaxValue() {
        return rightMaxValue;
    }

    public SkillBean setRightMaxValue(int rightMaxValue) {
        this.rightMaxValue = rightMaxValue;
        return this;
    }

    public void addJewelry(SkillJewelryBean bean) {
        if (jewelryList==null)
            jewelryList=new ArrayList<>();
         jewelryList.add(bean);
    }

    public void setType(int type) {
        this.type = type;
    }

    public static class SkillEffect implements Comparable<SkillEffect> {
        private String name;
        @SerializedName("effect")
        private String description;
        private int value;

        public String getName() {
            return name;
        }

        public SkillEffect setName(String name) {
            this.name = name;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public SkillEffect setDescription(String description) {
            this.description = description;
            return this;
        }

        public int getValue() {
            return value;
        }

        public SkillEffect setValue(int value) {
            this.value = value;
            return this;
        }

        @Override
        public int compareTo(SkillEffect o) {
            return value - o.value;
        }
    }
}
