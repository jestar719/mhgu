package cn.jestar.convert.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 花京院 on 2019/1/31.
 */

public class SkillBean {
    private int id;
    private String name;
    private String description;
    private List<SkillEffect> effectList = new ArrayList<>();
    private int leftMaxValue;
    private int rightMaxValue;
    private String url;


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

    public List<SkillEffect> getEffectList() {
        return effectList;
    }

    public SkillBean setEffectList(List<SkillEffect> effectList) {
        this.effectList = effectList;
        return this;
    }

    public SkillBean addSkillEffect(SkillEffect effet) {
        effectList.add(effet);
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

    public static class SkillEffect implements Comparable<SkillEffect> {
        private String name;
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
