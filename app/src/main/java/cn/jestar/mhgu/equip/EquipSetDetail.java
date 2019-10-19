package cn.jestar.mhgu.equip;

import java.util.ArrayList;
import java.util.List;

import cn.jestar.db.bean.BaseEquip;
import cn.jestar.db.bean.BaseSkill;
import cn.jestar.db.bean.Equip;
import cn.jestar.db.bean.Jewelry;
import cn.jestar.db.bean.Skill;

/**
 * 装备记录详情
 * Created by 花京院 on 2019/10/5.
 */

public class EquipSetDetail extends BaseEquipSet {
    private Equip[] equips;
    private List<List<Jewelry>> jewelries;
    private Skill[] skills;

    public EquipSetDetail() {
    }

    public EquipSetDetail(BaseEquipSet recode) {
        maxDefend = recode.maxDefend;
        amuletSlot = recode.amuletSlot;
        weaponSlot = recode.weaponSlot;
        amuletSkill = recode.amuletSkill;
        equipSkill = recode.equipSkill;
    }

    public Equip[] getEquips() {
        return equips;
    }

    public void setEquips(Equip[] equips) {
        this.equips = equips;
    }

    public List<List<Jewelry>> getJewelries() {
        return jewelries;
    }

    public void setJewelries(List<List<Jewelry>> jewelries) {
        this.jewelries = jewelries;
    }

    public Skill[] getSkills() {
        return skills;
    }

    public void setSkills(Skill[] skills) {
        this.skills = skills;
    }

    public EquipSetRecode getEquipSetRecode() {
        int[] ids = new int[5];
        for (int i = 0; i < 5; i++) {
            BaseEquip equip = equips[i];
            ids[i] = equip.getId();
        }
        int size = jewelries.size();
        List<int[]> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            List<Jewelry> jewelries = this.jewelries.get(i);
            int[] id = null;
            if (jewelries != null) {
                int num = jewelries.size();
                id = new int[num];
                for (int index = 0; index < num; index++) {
                    id[index] = jewelries.get(index).getId();
                }
            }
            list.add(i, id);
        }
        EquipSetRecode recode = new EquipSetRecode();
        recode.setIds(ids);
        recode.setJewelryIds(list);
        List<BaseSkill> skills1 = new ArrayList<>(Constans.SKILL_NUM);
        for (int i = 0; i < 2; i++) {
            Skill skill = skills[i];
            if (skill != null) {
                BaseSkill baseSkill = new BaseSkill(skill.getName(), skill.getValue());
                baseSkill.setId(skill.getId());
                skills1.add(skill);
            }
        }
        recode.setSkillValues(skills1);
        return recode;
    }
}
