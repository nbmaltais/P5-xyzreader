package com.example.xyzreader.utility;

import android.support.v7.graphics.Palette;

/**
 * Created by Nicolas on 2015-10-11.
 */
public class ColorResolver {

    private Palette mPalette;
    private int mDominantColor;
    private int mDominantBodyTextColor;
    private int mDominantTitleTextColor;
    private int mAccentColor;
    private int mAccentBodyTextColor;
    private int mAccentTitleTextColor;

    private Palette.Swatch mDominantSwatch;
    private Palette.Swatch mAccentSwatch;


    public ColorResolver(Palette palette)
    {
        mPalette = palette;
        resolveColors(mPalette);
    }


    private void resolveColors( Palette palette )
    {

        int population = 0;
        mDominantSwatch = findDominantSwatch(palette);

        if(mDominantSwatch==null)
        {
            // Find the fisrt non null
            //TODO: priorize the swatch
            if(palette.getVibrantSwatch()!=null)
            {
                mDominantSwatch = palette.getVibrantSwatch();
            }
            else if(palette.getDarkVibrantSwatch() != null)
            {
                mDominantSwatch =  palette.getDarkVibrantSwatch();
            }
            else if(palette.getMutedSwatch() != null )
            {
                mDominantSwatch =  palette.getMutedSwatch();
            }
            else if(palette.getDarkMutedSwatch() != null)
            {
                mDominantSwatch =  palette.getDarkMutedSwatch();
            }
        }


        mAccentSwatch = findAccentSwatch(palette,mDominantSwatch);

        mDominantColor = mDominantSwatch.getRgb();
        mDominantBodyTextColor = mDominantSwatch.getBodyTextColor();
        mDominantTitleTextColor = mDominantSwatch.getTitleTextColor();
        mAccentColor = mAccentSwatch.getRgb();
        mAccentBodyTextColor = mAccentSwatch.getBodyTextColor();
        mAccentTitleTextColor = mAccentSwatch.getTitleTextColor();

        //return results;
    }

    static protected Palette.Swatch findAccentSwatch( Palette palette, Palette.Swatch dominantSwatch )
    {
        Palette.Swatch accentSwatch = null;

        if( dominantSwatch !=  palette.getVibrantSwatch() && palette.getVibrantSwatch()!=null)
        {
            accentSwatch = palette.getVibrantSwatch();
        }
        else if( dominantSwatch !=  palette.getMutedSwatch() && palette.getMutedSwatch() !=null )
        {
            accentSwatch = palette.getMutedSwatch();
        }
        else if(  dominantSwatch !=  palette.getDarkMutedSwatch() && palette.getDarkMutedSwatch() !=null )
        {
            accentSwatch = palette.getDarkMutedSwatch();
        }
        else if( dominantSwatch !=  palette.getDarkVibrantSwatch() &&palette.getDarkVibrantSwatch() !=null )
        {
            accentSwatch = palette.getDarkVibrantSwatch();
        }
        else
        {
            accentSwatch = null;
        }

        return accentSwatch;
    }

    static protected Palette.Swatch findDominantSwatch( Palette palette )
    {

        int population = 0;
        Palette.Swatch selectedSwatch = null;
        double pop = 0;
        double VIBRANT_FACTOR = 1;
        double VIBRANT_DARK_FACTOR = 0.8;
        double MUTED_FACTOR = 0.4;
        double MUTED_DARK_FACTOR = 0.6;

        if(palette.getVibrantSwatch()!=null)
        {
            //Log.v(TAG,"  VibrantSwatch pop     = " + mPalette.getVibrantSwatch().getPopulation());
            pop = palette.getVibrantSwatch().getPopulation() * VIBRANT_FACTOR;
            selectedSwatch = palette.getVibrantSwatch();
        }
        if(palette.getDarkVibrantSwatch() != null && palette.getDarkVibrantSwatch().getPopulation() * VIBRANT_DARK_FACTOR > pop)
        {
            //Log.v(TAG,"  DarkVibrantSwatch pop = " + palette.getDarkVibrantSwatch().getPopulation());
            pop = palette.getDarkVibrantSwatch().getPopulation() * VIBRANT_DARK_FACTOR ;
            selectedSwatch =  palette.getDarkVibrantSwatch();
        }
        if(palette.getMutedSwatch() != null && palette.getMutedSwatch().getPopulation() * MUTED_FACTOR > pop)
        {
            //Log.v(TAG,"  MutedSwatch pop       = " + palette.getMutedSwatch().getPopulation());
            pop = palette.getMutedSwatch().getPopulation() * MUTED_FACTOR;
            selectedSwatch =  palette.getMutedSwatch();
        }
        if(palette.getDarkMutedSwatch() != null && palette.getDarkMutedSwatch().getPopulation()* MUTED_DARK_FACTOR > pop)
        {
            //Log.v(TAG,"  DarkMutedSwatch pop    = " + palette.getDarkMutedSwatch().getPopulation());
            pop = palette.getDarkMutedSwatch().getPopulation() * MUTED_DARK_FACTOR;
            selectedSwatch =  palette.getDarkMutedSwatch();
        }

        return selectedSwatch;
    }

    public int getDominantColor() {
        return mDominantColor;
    }

    public int getDominantBodyTextColor() {
        return mDominantBodyTextColor;
    }

    public int getDominantTitleTextColor() {
        return mDominantTitleTextColor;
    }

    public int getAccentColor() {
        return mAccentColor;
    }

    public int getAccentBodyTextColor() {
        return mAccentBodyTextColor;
    }

    public int getAccentTitleTextColor() {
        return mAccentTitleTextColor;
    }
}
