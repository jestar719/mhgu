package cn.jestar.mhgu.equip;

import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.jestar.db.bean.Jewelry;
import cn.jestar.mhgu.R;

import static cn.jestar.mhgu.equip.BaseEvent.Type.JEWELRY;

/**
 * 技能珠界面的管理
 * Created by 花京院 on 2019/10/5.
 */

public class JewelryManager {
    public static final String NULL = "+";
    public static final int MAX_SLOT = 3;
    protected int mSlotNum = MAX_SLOT;
    protected int mUsedSlot;
    protected TextView[] mViews = new TextView[mSlotNum];
    protected Jewelry[] mJewelries = new Jewelry[mSlotNum];
    protected OnSelectEventListener mListener;
    protected int mPart;
    protected int mJewelryNum;
    private ImageView mIvName;

    public JewelryManager(int part, ViewGroup group, OnSelectEventListener listener) {
        mListener = listener;
        mPart = part;
        init(group);
    }

    /**
     * 初始化技能珠及其点击事件
     *
     * @param group
     */
    @CallSuper
    public void init(ViewGroup group) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = (int) v.getTag();
                TextView view = mViews[index];
                boolean remove = isRemove(view);
                SelectEvent state = new SelectEvent(mPart, remove, JEWELRY);
                state.setIndex(index);
                state.setJewelry(mJewelries[index]);
                mListener.onSelectEvent(state);
                if (remove) {
                    removeJewelry(index);
                }
            }
        };
        initTvJewelry(group, R.id.tv_j1, 0, listener);
        initTvJewelry(group, R.id.tv_j2, 1, listener);
        initTvJewelry(group, R.id.tv_j3, 2, listener);
        mIvName = group.findViewById(R.id.iv_name);
    }

    public void setImg(@DrawableRes int src) {
        mIvName.setImageResource(src);
    }

    /**
     * 初始化技能珠按键
     *
     * @param group
     * @param id
     * @param index
     * @param listener
     */
    public void initTvJewelry(ViewGroup group, @IdRes int id, int index, View.OnClickListener listener) {
        TextView tv = group.findViewById(id);
        setText(tv, null);
        mViews[index] = tv;
        tv.setOnClickListener(listener);
        tv.setTag(index);
        if (index != 0) {
            tv.setVisibility(View.GONE);
        }
    }

    /**
     * 移除还是添加
     *
     * @param view 被点击的View
     * @return true表示移除, false为添加
     */
    protected boolean isRemove(TextView view) {
        return !NULL.equals(view.getText());
    }

    /**
     * 添加技能珠
     *
     * @param index   角标
     * @param jewelry 技能珠
     */
    public boolean setJewelry(int index, Jewelry jewelry) {
        int slotNum = jewelry.getSlotNum();
        if (mUsedSlot + slotNum > mSlotNum) {
            return false;
        }
        mJewelryNum++;
        mJewelries[index] = jewelry;
        mUsedSlot += slotNum;
        setText(mViews[index], jewelry.getName());
        if (mUsedSlot < mSlotNum && index != mSlotNum - 1) {
            mViews[index + 1].setVisibility(View.VISIBLE);
        }
        return true;
    }

    /**
     * 移除技能珠
     *
     * @param index 角标
     */
    private void removeJewelry(int index) {
        Jewelry jewelry = mJewelries[index];
        mJewelries[index] = null;
        mUsedSlot -= jewelry.getSlotNum();
        mJewelryNum--;
        if (mJewelryNum != 0) {
            sortJewelry();
        }
        resetJewelry();
    }

    /**
     * 排序技能珠,从左往右
     */
    private void sortJewelry() {
        Jewelry[] jewelries = new Jewelry[MAX_SLOT];
        int i = 0;
        for (Jewelry jewel : mJewelries) {
            if (jewel != null) {
                jewelries[i] = jewel;
                i++;
            }
        }
        mJewelries = jewelries;
    }

    /**
     * 根据技能珠设置按键的文本,及显示隐藏.
     */
    public void resetJewelry() {
        for (int i = 0; i < mJewelries.length; i++) {
            Jewelry jewel = mJewelries[i];
            TextView view = mViews[i];
            boolean isNull = jewel == null;
            String text = isNull ? null : jewel.getName();
            setText(view, text);
            view.setVisibility(isNull ? View.GONE : View.VISIBLE);
        }
        if (mUsedSlot < mSlotNum) {
            for (TextView view : mViews) {
                if (view.getVisibility() == View.GONE) {
                    view.setVisibility(View.VISIBLE);
                    return;
                }
            }
        }
    }

    /**
     * 清空所有技能珠
     */
    public void clearJewelry() {
        mJewelries = new Jewelry[MAX_SLOT];
        mUsedSlot = 0;
        resetJewelry();
    }

    protected void setText(TextView view, String text) {
        boolean isSelect = text == null;
        view.setText(isSelect ? NULL : text);
        view.setSelected(isSelect);
    }

    public void clear() {
        clearJewelry();
    }
}
