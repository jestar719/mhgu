package com.zhy.view.flowlayout;

import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class TagAdapter<T> {
    private final List<T> mData;
    private final HashSet<Integer> mCheckedPosList = new HashSet<>();
    private OnDataChangedListener mOnDataChangedListener;

    public TagAdapter(List<T> data) {
        mData = data == null ? new ArrayList<T>() : data;
    }

    public TagAdapter(T[] data) {
        this(data == null ? null : Arrays.asList(data));
    }

    public int getCount() {
        return mData.size();
    }

    public T getItem(int position) {
        return mData.get(position);
    }

    public Set<Integer> getPreCheckedList() {
        return new HashSet<>(mCheckedPosList);
    }

    public void setSelectedList(int... poses) {
        mCheckedPosList.clear();
        if (poses != null) {
            for (int pos : poses) {
                mCheckedPosList.add(pos);
            }
        }
        notifyDataChanged();
    }

    public void setSelectedList(Set<Integer> set) {
        mCheckedPosList.clear();
        if (set != null) {
            mCheckedPosList.addAll(set);
        }
        notifyDataChanged();
    }

    void setOnDataChangedListener(OnDataChangedListener listener) {
        mOnDataChangedListener = listener;
    }

    protected void notifyDataChanged() {
        if (mOnDataChangedListener != null) {
            mOnDataChangedListener.onChanged();
        }
    }

    public abstract View getView(FlowLayout parent, int position, T item);

    interface OnDataChangedListener {
        void onChanged();
    }
}
