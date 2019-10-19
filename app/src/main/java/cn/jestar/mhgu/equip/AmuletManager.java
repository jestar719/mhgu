package cn.jestar.mhgu.equip;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.jestar.db.bean.BaseSkill;
import cn.jestar.db.bean.Skill;
import cn.jestar.mhgu.R;

import static cn.jestar.db.bean.BaseEquip.PART.AMULET;
import static cn.jestar.mhgu.equip.BaseEvent.Type.SKILL;

/**
 * 护符界面管理类
 * Created by 花京院 on 2019/10/5.
 */

public class AmuletManager extends JewelryManager {

    private final int MASK = 1;
    private TextView[] mTvSkills = new TextView[2];
    private TextView[] mTvSkillValues = new TextView[2];
    private Skill[] mSkills = new Skill[2];
    private int[] mSkillValues = new int[2];
    private View[] mAddViews;


    public AmuletManager(int part, ViewGroup group, OnSelectEventListener listener) {
        super(part, group, listener);
        initSkill(group);
        mTvSkillValues[0] = group.findViewById(R.id.tv_skill1_value);
        mTvSkillValues[1] = group.findViewById(R.id.tv_skill2_value);
        for (TextView view : mTvSkillValues) {
            view.setText("0");
        }
    }


    /**
     * 初始化技能按键
     */
    private void initSkill(ViewGroup group) {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = (int) v.getTag();
                boolean remove = isRemove(mTvSkills[index]);
                SelectEvent state = new SelectEvent(mPart, remove, SKILL);
                state.setIndex(index);
                mListener.onSelectEvent(state);
                if (remove) {
                    setSkill(null, index);
                }
            }
        };
        initSkill(group, R.id.tv_skill1, 0, onClickListener);
        initSkill(group, R.id.tv_skill2, 1, onClickListener);
        initSkillValueModify(group);
    }

    private void initSkill(ViewGroup group, int id, int index, View.OnClickListener listener) {
        TextView view = group.findViewById(id);
        view.setTag(index);
        mTvSkills[index] = view;
        setText(view, null);
        view.setOnClickListener(listener);
    }

    /**
     * 初始化技能数值加减的按键
     */
    private void initSkillValueModify(ViewGroup group) {
        mAddViews = new View[4];
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tag = (int) v.getTag();
                int index = (tag >> 1) & MASK;
                boolean isAdd = (tag & MASK) == 1;
                Skill skill = mSkills[index];
                if (skill != null) {
                    int value = mSkillValues[index];
                    int max = isAdd ? skill.getMax() : 0;
                    if ((isAdd && value < max) || (value > max && !isAdd)) {
                        value += (isAdd ? 1 : -1);
                        mSkillValues[index] = value;
                        mTvSkillValues[index].setText(String.valueOf(value));
                        SelectEvent event = new SelectEvent(AMULET, false, SKILL);
                        event.setIndex(index);
                        BaseSkill baseSkill = new BaseSkill(skill.getName(), value);
                        event.setSkill(baseSkill);
                        mListener.onSelectEvent(event);
                        int i = tag + (isAdd ? -1 : 1);
                        View addView = mAddViews[i];
                        if (!addView.isSelected()) {
                            addView.setSelected(true);
                        }
                        if (value == max)
                            v.setSelected(false);
                    }
                }
            }
        };
        initAdd(group, R.id.tv_skill1_reduce, 0, listener);
        initAdd(group, R.id.tv_skill1_add, 1, listener);
        initAdd(group, R.id.tv_skill2_reduce, 2, listener);
        initAdd(group, R.id.tv_skill2_add, 3, listener);
    }

    private void initAdd(ViewGroup group, int id, int index, View.OnClickListener listener) {
        TextView view = group.findViewById(id);
        view.setTag(index);
        view.setOnClickListener(listener);
        mAddViews[index] = view;
    }


    /**
     * 设置技能 设置/移除
     *
     * @param skill 技能
     * @param index 角标
     */
    public void setSkill(Skill skill, int index) {
        boolean isNull = skill == null;
        String name = isNull ? null : skill.getName();
        setText(mTvSkills[index], name);
        mSkills[index] = skill;
        int value = isNull ? 0 : skill.getValue();
        mTvSkillValues[index].setText(String.valueOf(value));
        mSkillValues[index] = value;
        int i = index * 2;
        mAddViews[i].setSelected(false);
        mAddViews[i + 1].setSelected(!isNull);
    }

    @Override
    public void clear() {
        super.clear();
        for (int i = 0; i < mSkills.length; i++) {
            setSkill(null, i);
        }
    }
}
