package cn.jestar.db.bean;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import java.util.List;

/**
 * 完整的技能数据
 * Created by 花京院 on 2019/10/3.
 */
@Entity
public class Skill extends BaseSkill {
    @Ignore
    public List<Jewelry> jewelryList;
    public int leftMaxValue;
    public int rightMaxValue;
    public String url;
    public int type;
    @Ignore
    protected List<SkillEffect> effectList;

    public Skill() {
    }

    public Skill(Skill skill) {
        super(skill.name, skill.value);
        this.effectList = skill.effectList;
        this.jewelryList = skill.jewelryList;
        this.leftMaxValue = skill.leftMaxValue;
        this.rightMaxValue = skill.rightMaxValue;
        this.url = skill.url;
        this.type = skill.type;
    }

    public List<SkillEffect> getEffectList() {
        return effectList;
    }

    public void setEffectList(List<SkillEffect> effectList) {
        this.effectList = effectList;
        ;
    }

    public List<Jewelry> getJewelryList() {
        return jewelryList;
    }

    public void setJewelryList(List<Jewelry> jewelryList) {
        this.jewelryList = jewelryList;
    }

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

    public int getMax() {
        return Math.max(leftMaxValue, rightMaxValue);
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

    public SkillEffect getEffect(int value) {
        SkillEffect effect = null;
        int size = effectList.size();
        if (value >= 10) {
            for (int i = 0; i < size; i++) {
                SkillEffect skillEffect = effectList.get(i);
                if (value >= skillEffect.getValue()) {
                    effect = skillEffect;
                    break;
                }
            }
        } else if (value <= -10) {
            for (int i = size - 1; i >= 0; i--) {
                SkillEffect skillEffect = effectList.get(i);
                if (value <= skillEffect.getValue()) {
                    effect = skillEffect;
                    break;
                }
            }
        }
        return effect;
    }
}
