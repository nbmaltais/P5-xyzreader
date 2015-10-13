package com.example.xyzreader.utility;

import android.support.v7.graphics.Palette;

/**
 * Created by Nicolas on 2015-10-13.
 */
public class DarkMutedColorResolver extends ColorResolver {

    public DarkMutedColorResolver(Palette palette) {
        super(palette);
    }

    @Override
    protected void resolveColors(Palette palette) {
        if(palette.getMutedSwatch()!=null)
            mPrimarySwatch=palette.getMutedSwatch();
        else if(palette.getLightMutedSwatch()!=null)
            mPrimarySwatch=palette.getLightMutedSwatch();
        else if(palette.getDarkMutedSwatch() !=null)
            mPrimarySwatch=palette.getDarkMutedSwatch();

        if(palette.getVibrantSwatch()!=null)
            mAccentSwatch=palette.getVibrantSwatch();
        else if(palette.getLightVibrantSwatch() !=null)
            mAccentSwatch=palette.getLightVibrantSwatch();
        else if(palette.getDarkVibrantSwatch() !=null)
            mAccentSwatch=palette.getDarkVibrantSwatch();
    }


}
