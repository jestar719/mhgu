package com.zhy.view.flowlayout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.Checkable;
import android.widget.FrameLayout;

public class TagView extends FrameLayout implements Checkable {
    private static final int[] CHECK_STATE = {android.R.attr.state_checked};
    private boolean mChecked;

    public TagView(Context context) {
        super(context);
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            refreshDrawableState();
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] states = super.onCreateDrawableState(extraSpace + 1);
        if (mChecked) {
            mergeDrawableStates(states, CHECK_STATE);
        }
        return states;
    }

    public void moveChildBackgroundToSelf() {
        if (getChildCount() == 0) {
            return;
        }
        Drawable background = getChildAt(0).getBackground();
        if (background == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getChildAt(0).setBackground(null);
            setBackground(background);
        } else {
            //noinspection deprecation
            getChildAt(0).setBackgroundDrawable(null);
            //noinspection deprecation
            setBackgroundDrawable(background);
        }
    }
}
