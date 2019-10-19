package cn.jestar.db.bean;

/**
 * 技能基类
 * Created by 花京院 on 2019/9/19.
 */

public class BaseSkill extends BaseBean {
    public int value;

    public BaseSkill() {
    }

    public BaseSkill(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "BaseSkill{" +
                "value=" + value +
                ", id=" + id +
                ", name='" + name + '\'' +
                "} ";
    }
}
