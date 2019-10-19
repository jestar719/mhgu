package cn.jestar.mhgu.equip;

import android.arch.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jestar.db.bean.BaseEquip;
import cn.jestar.db.bean.BaseSkill;
import cn.jestar.db.bean.Equip;
import cn.jestar.db.bean.EquipSkill;
import cn.jestar.db.bean.Jewelry;
import cn.jestar.db.bean.Skill;
import cn.jestar.mhgu.AppManager;

import static cn.jestar.db.bean.BaseEquip.PART.AMULET;
import static cn.jestar.db.bean.BaseEquip.PART.WEAPON;
import static cn.jestar.db.bean.BaseEquip.TYPE.FIGHT;

/**
 * 装备技能统计
 * 保存装备,技能珠,技能数据,并统计对应的技能数据
 * Created by 花京院 on 2019/10/6.
 */

public class EquipSKillSummer {
    private SkillRepository mRepository;
    private Equip[] mEquips = new Equip[5];
    private Jewelry[][] mJewelries = new Jewelry[3][7];
    private BaseSkill[] mSkills = new BaseSkill[2];
    private Map<String, SumSkill> mSumSkills = new HashMap<>();
    private MutableLiveData<EquipSetValue> mData = new MutableLiveData<EquipSetValue>();

    public EquipSKillSummer(SkillRepository repository) {
        mRepository = repository;
    }

    public MutableLiveData<EquipSetValue> getData() {
        return mData;
    }

    /**
     * 添加装备,根据装备部位移除已保存的装备并异步获取该装备的完整数据
     *
     * @param equip 基础装备数据
     */
    public void addEquip(BaseEquip equip) {
        int part = equip.getPart();
        onRemoveEquip(part);
        mRepository.getEquip(equip);
    }

    /**
     * 当获取到完整的装备数据时,添加装备并刷新技能统计
     *
     * @param equip 完整的装备数据
     */
    public void onGetEquip(Equip equip) {
        onAddEquip(equip);
        notifyDataChange();
    }

    /**
     * 添加装备及其所带的技能
     *
     * @param equip 完整的装备数据
     */
    private void onAddEquip(Equip equip) {
        int part = equip.getPart();
        mEquips[part] = equip;
        List<EquipSkill> skills = equip.getSkills();
        for (EquipSkill skill : skills) {
            String name = skill.getName();
            SumSkill sumSkill = getSumSkill(name);
            sumSkill.add(part, skill.getValue());
        }
    }

    /**
     * 移除装备并刷新统计数据
     *
     * @param part 装备部位
     */
    public void removeEquip(int part) {
        onRemoveEquip(part);
        notifyDataChange();
    }


    /**
     * 根据部位移除装备,并移除该部位的技能珠
     *
     * @param part 部位
     */
    private void onRemoveEquip(int part) {
        Equip equip = mEquips[part];
        if (equip != null) {
            List<EquipSkill> skills = equip.getSkills();
            for (EquipSkill skill : skills) {
                String name = skill.getName();
                SumSkill sumSkill = getSumSkill(name);
                sumSkill.reduce(part, skill.getValue());
            }
            onRemoveJewelryInPart(part);
            mEquips[part] = null;
        }
    }

    /**
     * 添加技能珠,如果技能珠的技能已经缓存,则直接添加技能珠.
     * 否则先缓存对应的技能,再进行添加
     *
     * @param jewelry 技能球
     * @param part    部位
     */
    public void addJewelry(Jewelry jewelry, int part) {
        if (mRepository.isCachedSkill(jewelry)) {
            onAddJewelry(jewelry, part);
            notifyDataChange();
        } else {
            AddJewelryThread thread = new AddJewelryThread(jewelry, part);
            AppManager.getExecutor().execute(thread);
        }
    }

    /**
     * 根据部位添在首个空位置加技能珠
     *
     * @param j    技能珠
     * @param part 部位
     */
    private void onAddJewelry(Jewelry j, int part) {
        for (int i = 0; i < Constans.MAX_SLOT_NUM; i++) {
            Jewelry jewelry = mJewelries[i][part];
            if (jewelry == null) {
                mJewelries[i][part] = j;
                String name = j.getSkillName();
                SumSkill sumSkill = getSumSkill(name);
                sumSkill.add(part, j.getValue());
                name = j.getDebuff();
                if (name != null) {
                    sumSkill = getSumSkill(name);
                    sumSkill.reduce(part, j.getDebuffValue());
                }
                return;
            }
        }
    }

    /**
     * 根据部位移除对应角标的技能珠,重排序技能珠并刷新技能统计
     *
     * @param part  部位
     * @param index 角标
     */
    public void removeJewelry(int part, int index) {
        onRemoveJewelry(part, index);
        Jewelry[] array = new Jewelry[Constans.MAX_SLOT_NUM];
        int start = 0;
        for (int i = 0; i < Constans.MAX_SLOT_NUM; i++) {
            Jewelry jewelry = mJewelries[i][part];
            if (jewelry != null) {
                array[start] = jewelry;
                start++;
            }
        }
        for (int i = 0; i < array.length; i++) {
            mJewelries[i][part] = array[i];
        }
        notifyDataChange();
    }


    /**
     * 移除技能珠并修改对应的技能数值
     *
     * @param part  部位
     * @param index 角标
     */
    private void onRemoveJewelry(int part, int index) {
        Jewelry jewelry = mJewelries[index][part];
        if (jewelry != null) {
            String name = jewelry.getSkillName();
            getSumSkill(name).reduce(part, jewelry.getValue());
            name = jewelry.getDebuff();
            if (name != null)
                getSumSkill(name).add(part, jewelry.getDebuffValue());
        }
        mJewelries[index][part] = null;
    }

    /**
     * 根据角标加技能,如果该技能未缓存,则异步缓存该技能完整数据后再添加并刷新技能统计
     *
     * @param index 技能角标
     * @param skill 技能数据
     */
    public void addSkill(int index, BaseSkill skill) {
        if (mRepository.getCachedSkill(skill.getName()) != null) {
            onAddSkill(index, skill);
            notifyDataChange();
        } else {
            AppManager.getExecutor().execute(new AddSkillThread(skill, index));
        }
    }

    /**
     * 根据角标先移除该位置的技能再添加技能数据
     *
     * @param index 角标
     * @param skill 技能
     */
    private void onAddSkill(int index, BaseSkill skill) {
        onRemoveSkill(index);
        SumSkill sumSkill = getSumSkill(skill.getName());
        sumSkill.add(AMULET, skill.getValue());
        mSkills[index] = skill;
    }

    /**
     * 移除技能并刷新技能统计
     *
     * @param index 角标
     */
    public void removeSkill(int index) {
        onRemoveSkill(index);
        notifyDataChange();
    }

    /**
     * 移除技能
     *
     * @param index 角标
     */
    private void onRemoveSkill(int index) {
        BaseSkill skill = mSkills[index];
        if (skill != null) {
            getSumSkill(skill.getName()).reduce(AMULET, skill.getValue());
            mSkills[index] = null;
        }
    }

    /**
     * 清空数据
     */
    public void clear() {
        mSumSkills.clear();
        mEquips = new Equip[5];
        mSkills = new BaseSkill[2];
        mJewelries = new Jewelry[3][7];
    }

    /**
     * 清空数据并刷新技能统计
     */
    public void clearAll() {
        clear();
        notifyDataChange();
    }

    /**
     * 清空所有的装备并刷新统计数据
     */
    public void clearEquip() {
        for (int i = 0; i < mEquips.length; i++) {
            onRemoveEquip(i);
        }
        notifyDataChange();
    }

    /**
     * 清空技能珠并刷新技能统计
     */
    public void clearJewelry() {
        for (int i = 0; i < 7; i++) {
            onRemoveJewelryInPart(i);
        }
        notifyDataChange();
    }

    /**
     * 根据部位清除技能珠
     *
     * @param part 部位
     */
    private void onRemoveJewelryInPart(int part) {
        for (int i = 0; i < Constans.MAX_SLOT_NUM; i++) {
            onRemoveJewelry(part, i);
        }
    }


    /**
     * 根据技能名获取技能的统计对象{@link SumSkill}
     *
     * @param name 技能名
     * @return 技能的统计对象
     */
    private SumSkill getSumSkill(String name) {
        SumSkill sumSkill = mSumSkills.get(name);
        if (sumSkill == null) {
            Skill skill1 = mRepository.getCachedSkill(name);
            sumSkill = new SumSkill(skill1);
            mSumSkills.put(name, sumSkill);
        }
        return sumSkill;
    }

    /**
     * 统计并投递技能数据
     */
    public void notifyDataChange() {
        ArrayList<SumSkill> list = new ArrayList<>();
        for (SumSkill skill : mSumSkills.values()) {
            if (skill.getValue() != 0)
                list.add(skill);
        }
        int[] values = new int[Constans.EQUIP_VALUE_NUM];
        for (Equip equip : mEquips) {
            if (equip != null)
                setEquipValue(equip, values);
        }
        EquipSetValue equipSetValue = new EquipSetValue().setEquipValues(values).setSumSkillList(list);
        mData.postValue(equipSetValue);
    }

    /**
     * 设置装备数据
     *
     * @param equip  装备
     * @param values 装备数据
     */
    private void setEquipValue(Equip equip, int[] values) {
        sumValue(values, equip.getDefence(), 0);
        sumValue(values, equip.getMaxDefence(), 1);
        sumValue(values, equip.getFire(), 2);
        sumValue(values, equip.getWater(), 3);
        sumValue(values, equip.getIce(), 4);
        sumValue(values, equip.getFlash(), 5);
        sumValue(values, equip.getDragon(), 6);
    }

    private void sumValue(int[] values, int value, int index) {
        values[index] = values[index] + value;
    }

    /**
     * 保存配装数据,保存装备id/技能珠id/技能.
     * 根据胸部装备判断装备类型
     *
     * @param recode 配装备数据
     */
    public void saveEquipSet(EquipSetRecode recode) {
        int[] ids = new int[Constans.EQUIP_SUM];
        int defend = 0;
        for (int i = 0; i < mEquips.length; i++) {
            Equip equip = mEquips[i];
            ids[i] = equip.getId();
            defend += equip.getMaxDefence();
            if (i == 1) {
                recode.setFight(equip.getType() == FIGHT);
            }
        }
        recode.setIds(ids);
        recode.setMaxDefend(defend);
        List<int[]> jewelries = new ArrayList<>();
        for (int i = 0; i < Constans.PART_NUM; i++) {
            ids = new int[3];
            int slot = 0;
            for (int j = 0; j < Constans.MAX_SLOT_NUM; j++) {
                Jewelry jewelry = mJewelries[j][i];
                if (jewelry != null) {
                    ids[j] = jewelry.getId();
                    slot += jewelry.getSlotNum();
                }
            }
            if (i == AMULET) {
                recode.setAmuletSlot(slot);
            } else if (i == WEAPON) {
                recode.setWeaponSlot(slot);
            }
            jewelries.add(ids);
        }
        recode.setJewelryIds(jewelries);
        ArrayList<BaseSkill> list = new ArrayList<>();
        for (BaseSkill skill : mSkills) {
            if (skill != null)
                list.add(skill);
        }
        recode.setSkillValues(list);
        mRepository.saveEquipSet(recode);
    }


    /**
     * 加载配装并刷新统计数据
     *
     * @param detail 配装数据
     */
    public void loadEquipSet(EquipSetDetail detail) {
        clear();
        Equip[] equips = detail.getEquips();
        for (Equip equip : equips) {
            onAddEquip(equip);
        }
        List<List<Jewelry>> jewelries = detail.getJewelries();
        int size = jewelries.size();
        for (int i = 0; i < size; i++) {
            List<Jewelry> list = jewelries.get(i);
            if (list != null) {
                for (int j = 0; j < list.size(); j++) {
                    Jewelry jewelry = list.get(j);
                    onAddJewelry(jewelry, i);
                }
            }
        }
        Skill[] skills = detail.getSkills();
        for (int i = 0; i < skills.length; i++) {
            Skill skill = skills[i];
            if (skill != null) {
                onAddSkill(i, skill);
            }
        }
        notifyDataChange();
    }

    /**
     * 同步获取技能珠的技能,并添加技能珠
     */
    class AddJewelryThread implements Runnable {

        private Jewelry mJewelry;
        private int mPart;

        public AddJewelryThread(Jewelry jewelry, int part) {
            mJewelry = jewelry;
            mPart = part;
        }

        @Override
        public void run() {
            mRepository.querySkillByJewelrySync(mJewelry);
            addJewelry(mJewelry, mPart);
        }
    }

    /**
     * 同步添加技能
     */
    class AddSkillThread implements Runnable {

        private BaseSkill mSkill;
        private int mIndex;

        public AddSkillThread(BaseSkill skill, int index) {
            mSkill = skill;
            mIndex = index;
        }

        @Override
        public void run() {
            mRepository.getSkillSync(mSkill.getName());
            addSkill(mIndex, mSkill);
        }
    }
}
