package cn.jestar.mhgu.equip;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.jestar.db.bean.BaseEquip;
import cn.jestar.mhgu.R;

import static cn.jestar.mhgu.equip.BaseEvent.Type.EQUIP;

/**
 * 装备键管理
 * Created by 花京院 on 2019/10/5.
 */

public class EquipManager extends JewelryManager implements View.OnClickListener {
    private TextView mTvEquip;
    private TextView mTvSlotNum;

    public EquipManager(int part, ViewGroup group, OnSelectEventListener listener) {
        super(part, group, listener);
        mTvEquip = group.findViewById(R.id.tv_equip);
        mTvEquip.setVisibility(View.VISIBLE);
        mTvEquip.setOnClickListener(this);
        mTvSlotNum = group.findViewById(R.id.tv_slot_num);
        mTvSlotNum.setVisibility(View.VISIBLE);
        mSlotNum = 0;
        clear();
    }

    /**
     * 装备的点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        boolean remove = isRemove(mTvEquip);
        SelectEvent state = new SelectEvent(mPart, remove, EQUIP);
        mListener.onSelectEvent(state);
        if (remove) {
            clear();
        }
    }

    /**
     * 添加装备
     *
     * @param equip 装备数据
     */
    public void addEquip(BaseEquip equip) {
        setText(mTvEquip, equip == null ? null : equip.getName());
        mSlotNum = equip == null ? 0 : equip.getSlotNum();
        mTvSlotNum.setText(String.valueOf(mSlotNum));
        clearJewelry();
    }

    @Override
    public void clear() {
        addEquip(null);
    }
}
