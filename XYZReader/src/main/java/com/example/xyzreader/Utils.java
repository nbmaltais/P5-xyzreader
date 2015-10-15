package com.example.xyzreader;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

/**
 * Created by Nicolas on 2015-10-04.
 */
public class Utils {
    static public int getStatusBarHeight(Context ctx) {
        int result = 0;
        int resourceId = ctx.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = ctx.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static String makeImageTransitionName(long itemId) {
        return "Image" + Long.toString(itemId);
    }

    public static void scheduleStartPostponedTransition(final Activity activity, final View sharedElement) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElement.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                            Log.d("Utils", "scheduleStartPostponedTransition -> onPreDraw");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                activity.startPostponedEnterTransition();
                            }

                            return true;
                        }
                    });
        }
    }

    public static int getDisplayHeight(Context ctx)
    {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);  // deprecated
        return size.y;
    }

    public static void animateBackgroundColor( final View view , int colorFrom, int colorTo, long duration)
    {

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(duration);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((Integer)animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }

    public static void animateBackgroundTintList( final View view , int colorFrom, int colorTo, long duration)
    {

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(duration);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setBackgroundTintList(ColorStateList.valueOf((Integer) animator.getAnimatedValue()));
                }
            }

        });
        colorAnimation.start();
    }

    public static int getBackgroundColor(View view, int defaultColor) {
        Drawable background =  view.getBackground();
        if( background instanceof ColorDrawable)
        {
            return ((ColorDrawable)background).getColor();
        }
        else
            return defaultColor;
    }
}
