package com.example.xyzreader;

import android.content.Context;

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
}
