package com.example.xyzreader.utility;

import android.support.v7.graphics.Palette;

/**
 * Created by Nicolas on 2015-10-13.
 */
public abstract class ColorResolver {
    protected Palette.Swatch mPrimarySwatch;
    protected Palette.Swatch mAccentSwatch;

    public ColorResolver(Palette palette)
    {
        resolveColors(palette);
    }

    public boolean isResolved()
    {
        return mPrimarySwatch!=null && mAccentSwatch !=null;
    }

    public Palette.Swatch getPrimarySwatch() {
        return mPrimarySwatch;
    }

    public Palette.Swatch getAccentSwatch() {
        return mAccentSwatch;
    }

    protected abstract void resolveColors(Palette palette);
}
