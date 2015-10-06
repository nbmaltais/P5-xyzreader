package com.example.xyzreader.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by Nicolas on 2015-10-05.
 */
public class MaxWidthRelativeLayout extends RelativeLayout {
    private static final int[] ATTRS = {
            android.R.attr.maxWidth
    };

    private int mMaxWidth = Integer.MAX_VALUE;

    public MaxWidthRelativeLayout(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public MaxWidthRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public MaxWidthRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MaxWidthRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        mMaxWidth = a.getLayoutDimension(0, Integer.MAX_VALUE);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int newSpecWidth = Math.min(View.MeasureSpec.getSize(widthMeasureSpec), mMaxWidth);
        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(newSpecWidth, View.MeasureSpec.getMode(widthMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
