package com.bsaldevs.bsalarmer.CustomViews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.bsaldevs.bsalarmer.Utils;


public class CustomFrameLayout extends FrameLayout {
    public CustomFrameLayout(@NonNull Context context) {
        super(context);
    }

    public CustomFrameLayout(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(Utils.convertDiptoPix(450, getContext()), MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
