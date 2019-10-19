package cn.jestar.mhgu.equip;

import android.arch.core.util.Function;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import cn.jestar.db.bean.BaseEquip;
import cn.jestar.db.bean.BaseSkill;
import cn.jestar.db.bean.Equip;
import cn.jestar.db.bean.Jewelry;
import cn.jestar.db.bean.Skill;

import static cn.jestar.mhgu.equip.BaseEvent.Type.EQUIP;
import static cn.jestar.mhgu.equip.BaseEvent.Type.JEWELRY;
import static cn.jestar.mhgu.equip.BaseEvent.Type.SKILL;

/**
 * 配装界面的ViewModel
 * 作为Activity,Fragment,{@link SkillRepository},{@link EquipSKillSummer}之间的数据中转
 * Created by 花京院 on 2019/10/5.
 */

public class EquipModel extends ViewModel {
    private SkillRepository mRepository;
    private EquipSKillSummer mSummer;
    private MutableLiveData<MenuSelectEvent> mEventLiveData = new MutableLiveData<>();
    private MutableLiveData<String> mSkillSearchData = new MutableLiveData<>();
    private boolean mQueryBySkill;

    public EquipModel() {
        mRepository = new SkillRepository();
        mSummer = new EquipSKillSummer(mRepository);
    }

    /**
     * 移除事件,判断并调用技能统计对象的相关方法
     *
     * @param state 移除事件
     */
    public void onRemove(SelectEvent state) {
        int type = state.getType();
        if (type == SKILL) {
            mSummer.removeSkill(state.getIndex());
        } else if (type == EQUIP) {
            mSummer.removeEquip(state.getPart());
        } else if (type == JEWELRY) {
            mSummer.removeJewelry(state.getPart(), state.getIndex());
        }
    }

    public void addEquip(BaseEquip equip) {
        mSummer.addEquip(equip);
    }

    /**
     * 投递菜单选择事件{@link BaseEquipFragment#onItemClick(AdapterView, View, int, long)}
     *
     * @param event 菜单选择事件
     */
    public void postMenuSelectEvent(MenuSelectEvent event) {
        mEventLiveData.setValue(event);
    }


    /**
     * 修改护符数值
     *
     * @param skill 技能
     * @param index 技能角标
     */
    public void setSkill(BaseSkill skill, int index) {
        mSummer.addSkill(index, skill);
    }

    /**
     * 保存配装
     *
     * @param recode 配装数据
     */
    public void saveEquipSet(EquipSetRecode recode) {
        mSummer.saveEquipSet(recode);
    }


    public List<EquipSetRecode> getRecodes(boolean isDefault) {
        return mRepository.getRecodes(isDefault);
    }

    public void loadEquipSet(EquipSetRecode item) {
        mRepository.loadEquipSet(item);
    }

    public void clearEquip() {
        mSummer.clearEquip();
    }

    public void clearJewelry() {
        mSummer.clearJewelry();
    }

    public void clearAll() {
        mSummer.clearAll();
    }

    /**
     * 分发查询事件
     *
     * @param query 查询事件
     */
    public void onQuery(QueryEvent query) {
        int queryType = query.getQueryType();
        switch (queryType) {
            case SKILL:
                onQuerySkill(query);
            case EQUIP:
                onQueryEquip(query);
            case JEWELRY:
                onQueryJewelry(query);
        }
    }

    private void onQueryEquip(QueryEvent query) {
        mRepository.getEquips(query);
    }

    private void onQueryJewelry(QueryEvent query) {
        String input = query.getInput();
        if (input == null) {
            mRepository.queryJewelries(query.getType());
        } else {
            mRepository.getJewelry(input);
        }
    }

    private void onQuerySkill(QueryEvent query) {
        mRepository.getSkills(query.getType(), query.getInput());
    }

    public void addJewelry(Jewelry jewelry, int part) {
        mSummer.addJewelry(jewelry, part);
    }


    /**
     * 查询技能名包含该文本的技能
     *
     * @param text 查询文本
     */
    public void getLikedSkill(String text) {
        if (mQueryBySkill) {
            mSkillSearchData.setValue(text);
        }
    }

    /**
     * 设置技能查询类型
     *
     * @param isQueryBySkill true表示查询技能
     */
    public void setEquipQueryType(boolean isQueryBySkill) {
        mQueryBySkill = isQueryBySkill;
    }

    public void observerEvent(LifecycleOwner owner, Observer<MenuSelectEvent> observer) {
        mEventLiveData.observe(owner, observer);
    }

    public void observerSumSkill(LifecycleOwner owner, Observer<EquipSetValue> observer) {
        mSummer.getData().observe(owner, observer);
    }

    /**
     * 监听完整的装备数据
     *
     * @param owner {@link EquipSelectActivity#initModel()}
     */
    public void observerEquip(LifecycleOwner owner) {
        mRepository.getEquipData().observe(owner, new Observer<Equip>() {
            @Override
            public void onChanged(@Nullable Equip equip) {
                mSummer.onGetEquip(equip);
            }
        });
    }

    /**
     * 监听完整的配装数据,并刷新技能统计
     *
     * @param owner    {@link EquipSelectActivity#initModel()}
     * @param observer {@link EquipSelectActivity#initModel()}
     */
    public void observerEquipSet(LifecycleOwner owner, Observer<EquipSetDetail> observer) {
        Transformations.map(mRepository.getRecodeData(), new Function<EquipSetDetail, EquipSetDetail>() {
            @Override
            public EquipSetDetail apply(EquipSetDetail detail) {
                mSummer.loadEquipSet(detail);
                return detail;
            }
        }).observe(owner, observer);
    }

    public void observerJewelry(LifecycleOwner owner, Observer<List<Jewelry>> observer) {
        mRepository.getJewelriesData().observe(owner, observer);
    }

    public void observerSkills(LifecycleOwner owner, Observer<List<Skill>> observer) {
        mRepository.getSkillsData().observe(owner, observer);
    }

    public void observerEquips(LifecycleOwner owner, Observer<List<BaseEquip>> observer) {
        mRepository.getEquipsData().observe(owner, observer);
    }

    public void observeHistory(LifecycleOwner owner, Observer<List<String>> observer) {
        Transformations.switchMap(mSkillSearchData, new Function<String, LiveData<List<String>>>() {
            @Override
            public LiveData<List<String>> apply(String input) {
                return mRepository.getSkillNames(input);
            }
        }).observe(owner, observer);
    }

}
