package com.example.xyzreader.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.example.xyzreader.R;


public class FixedRatioImageView extends ImageView {
    private float mAspectRatio = 1.5f;

    public FixedRatioImageView(Context context) {
        super(context);
    }

    public FixedRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedRatioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAspectRatio(float aspectRatio) {
        mAspectRatio = aspectRatio;
        requestLayout();
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.FixedRatioImageView, defStyle, 0);
        assert a != null;

        mAspectRatio = a.getFloat(R.styleable.FixedRatioImageView_ratio, mAspectRatio);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        setMeasuredDimension(measuredWidth, (int) (measuredWidth / mAspectRatio));
    }
}
