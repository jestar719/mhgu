package com.zhy.view.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class FlowLayout extends ViewGroup {

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int maxWidth = widthMode == MeasureSpec.UNSPECIFIED ? Integer.MAX_VALUE : widthSize;
        int lineWidth = getPaddingLeft() + getPaddingRight();
        int lineHeight = 0;
        int wantedWidth = 0;
        int wantedHeight = getPaddingTop() + getPaddingBottom();

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, wantedHeight);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            if (lineWidth + childWidth > maxWidth && lineWidth > getPaddingLeft() + getPaddingRight()) {
                wantedWidth = Math.max(wantedWidth, lineWidth);
                wantedHeight += lineHeight;
                lineWidth = getPaddingLeft() + getPaddingRight() + childWidth;
                lineHeight = childHeight;
            } else {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
        }

        wantedWidth = Math.max(wantedWidth, lineWidth);
        wantedHeight += lineHeight;

        int measuredWidth = widthMode == MeasureSpec.EXACTLY ? widthSize : wantedWidth;
        int measuredHeight = heightMode == MeasureSpec.EXACTLY ? heightSize : wantedHeight;
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int x = getPaddingLeft();
        int y = getPaddingTop();
        int lineHeight = 0;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            int nextRight = x + lp.leftMargin + childWidth + lp.rightMargin + getPaddingRight();

            if (nextRight > width && x > getPaddingLeft()) {
                x = getPaddingLeft();
                y += lineHeight;
                lineHeight = 0;
            }

            int left = x + lp.leftMargin;
            int top = y + lp.topMargin;
            child.layout(left, top, left + childWidth, top + childHeight);

            x += lp.leftMargin + childWidth + lp.rightMargin;
            lineHeight = Math.max(lineHeight, lp.topMargin + childHeight + lp.bottomMargin);
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }
}
