package com.example.xyzreader.utility;

import android.support.v7.graphics.Palette;

/**
 * Created by Nicolas on 2015-10-11.
 * Class that tires to find the dominant color of an image i.e the 'background' color
 */
public class DominantColorResolver extends ColorResolver {


    public DominantColorResolver(Palette palette) {
        super(palette);
    }

    protected void resolveColors( Palette palette )
    {

        int population = 0;
        mPrimarySwatch = findDominantSwatch(palette);

        if(mPrimarySwatch==null)
        {
            // Find the fisrt non null
            //TODO: priorize the swatch
            if(palette.getVibrantSwatch()!=null)
            {
                mPrimarySwatch = palette.getVibrantSwatch();
            }
            else if(palette.getDarkVibrantSwatch() != null)
            {
                mPrimarySwatch =  palette.getDarkVibrantSwatch();
            }
            else if(palette.getMutedSwatch() != null )
            {
                mPrimarySwatch =  palette.getMutedSwatch();
            }
            else if(palette.getDarkMutedSwatch() != null)
            {
                mPrimarySwatch =  palette.getDarkMutedSwatch();
            }
        }


        mAccentSwatch = findAccentSwatch(palette,mPrimarySwatch);

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


}
