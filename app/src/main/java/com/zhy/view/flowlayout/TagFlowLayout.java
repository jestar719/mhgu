package com.zhy.view.flowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import cn.jestar.mhgu.R;

public class TagFlowLayout extends FlowLayout implements TagAdapter.OnDataChangedListener {
    private TagAdapter mTagAdapter;
    private final HashSet<Integer> mSelectedView = new HashSet<>();
    private int mMaxSelectCount = -1;
    private OnSelectListener mOnSelectListener;

    public TagFlowLayout(Context context) {
        this(context, null);
    }

    public TagFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagFlowLayout);
        mMaxSelectCount = ta.getInt(R.styleable.TagFlowLayout_max_select, -1);
        ta.recycle();
    }

    public void setAdapter(TagAdapter adapter) {
        mTagAdapter = adapter;
        if (mTagAdapter != null) {
            mTagAdapter.setOnDataChangedListener(this);
        }
        changeAdapter();
    }

    public void setMaxSelectCount(int count) {
        mMaxSelectCount = count;
    }

    public void setOnSelectListener(OnSelectListener listener) {
        mOnSelectListener = listener;
    }

    public Set<Integer> getSelectedList() {
        return Collections.unmodifiableSet(mSelectedView);
    }

    @Override
    public void onChanged() {
        changeAdapter();
    }

    private void changeAdapter() {
        removeAllViews();
        mSelectedView.clear();
        if (mTagAdapter == null) {
            return;
        }

        Set<Integer> preChecked = mTagAdapter.getPreCheckedList();
        for (int i = 0; i < mTagAdapter.getCount(); i++) {
            final int position = i;
            View tag = mTagAdapter.getView(this, i, mTagAdapter.getItem(i));
            TagView tagView = new TagView(getContext());
            tag.setDuplicateParentStateEnabled(true);
            ViewGroup.LayoutParams lp = tag.getLayoutParams();
            if (lp != null) {
                tagView.setLayoutParams(lp);
            }
            tagView.addView(tag);
            tagView.moveChildBackgroundToSelf();
            addView(tagView);

            if (preChecked.contains(i)) {
                setChildChecked(position, tagView, true);
            }

            tagView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    doSelect((TagView) v, position);
                }
            });
        }
    }

    private void doSelect(TagView child, int position) {
        if (child.isChecked()) {
            setChildChecked(position, child, false);
        } else {
            if (mMaxSelectCount == 1 && !mSelectedView.isEmpty()) {
                Integer[] selected = mSelectedView.toArray(new Integer[0]);
                for (Integer index : selected) {
                    TagView oldChild = (TagView) getChildAt(index);
                    if (oldChild != null) {
                        setChildChecked(index, oldChild, false);
                    }
                }
            } else if (mMaxSelectCount > 0 && mSelectedView.size() >= mMaxSelectCount) {
                return;
            }
            setChildChecked(position, child, true);
        }

        if (mTagAdapter != null) {
            mTagAdapter.setSelectedList(mSelectedView);
        }
        if (mOnSelectListener != null) {
            mOnSelectListener.onSelected(new HashSet<>(mSelectedView));
        }
    }

    private void setChildChecked(int position, TagView child, boolean checked) {
        child.setChecked(checked);
        if (checked) {
            mSelectedView.add(position);
        } else {
            mSelectedView.remove(position);
        }
    }

    public interface OnSelectListener {
        void onSelected(Set<Integer> selectPosSet);
    }
}
