package com.example.xyzreader.ui.detail;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.R;
import com.example.xyzreader.widget.FixedRatioImageView;

import java.util.HashMap;

import javax.xml.transform.Transformer;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ArticleDetailActivity.class.getSimpleName();
    private static final String EXTRA_TRANSITION_NAME = "TRANSITION_NAME";
    private static final String EXTRA_POSITION = "POSITION";
    private Cursor mCursor;
    private int mPosition;
    private long mItemId;


    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;

    private HashMap<Integer,ArticleDetailFragment> mPagerFragments = new HashMap<>();
    private boolean mPostponed=false;
    private ArticleDetailFragment mCurrentFragment;

    public static void start(Context ctx, int position, Uri uri, FixedRatioImageView thumbnailView) {
        Log.d(TAG,"Starting activity, uri = " + uri);
        Intent intent = new Intent(ctx,ArticleDetailActivity.class);
        intent.setData(uri);
        intent.putExtra(EXTRA_POSITION, position);

        if(thumbnailView!=null && ctx instanceof Activity)
        {
            String transitionName = ViewCompat.getTransitionName(thumbnailView);
            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) ctx, thumbnailView,ctx.getString(R.string.transition_main_image) ).toBundle();
            intent.putExtra(EXTRA_TRANSITION_NAME,transitionName);
            ctx.startActivity(intent,bundle);
        }
        else {

            ctx.startActivity(intent);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: put in resource

        if(savedInstanceState==null) {
            /*Log.d(TAG,"Postponing enter transition");
            mPostponed = true;
            ActivityCompat.postponeEnterTransition(this);*/
        }

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if(intent!=null) {
                mPosition = intent.getIntExtra(EXTRA_POSITION,0);
                if (intent.getData() != null) {
                    mItemId = ItemsContract.Items.getItemId(intent.getData());
                }
            }
        }
        else
        {
            // todo get position
        }


        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }*/
        setContentView(R.layout.activity_article_detail);

        getSupportLoaderManager().initLoader(0, null, this);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(true, new Transformer());
        //mPager.setCurrentItem(mPosition);
        // TODO: set in resource
        /*mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));*/

        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }

            @Override
            public void onPageSelected(int position) {
                if (mCursor != null) {
                    mCursor.moveToPosition(position);
                }
                mPosition = position;
                //mSelectedItemId = mCursor.getLong(ArticleLoader.Query._ID);
            }
        });



    }

    public void resumeContentTransitionAnimation( long id)
    {
        Log.d(TAG, "resumeContentTransitionAnimation, id = " + id + ", mItemId = " + mItemId);
        if(mPostponed && id == mItemId)
        {
            Log.d(TAG,"Starting postponed enter transition");
            mPostponed=false;
            ActivityCompat.startPostponedEnterTransition(this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mCursor = cursor;
        mPagerAdapter.notifyDataSetChanged();

        mPager.setCurrentItem(mPosition, false);
        /*// Select the start ID
        if (mStartId > 0) {
            mCursor.moveToFirst();
            // TODO: optimize
            while (!mCursor.isAfterLast()) {
                if (mCursor.getLong(ArticleLoader.Query._ID) == mStartId) {
                    final int position = mCursor.getPosition();
                    mPager.setCurrentItem(position, false);
                    break;
                }
                mCursor.moveToNext();
            }
            mStartId = 0;
        }*/
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }


    class Transformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View page, float position) {


            ArticleDetailFragment f = findFragment(page);
            if(f==null)
            {
                Log.w(TAG,"transformPage fragment is null");
                return;
            }

            f.transformPage(page,position);

        }
    }

    /**
     * Return the fragment associated with this view. Called from the view pager page transfromer
     * @param view
     * @return
     */
    private ArticleDetailFragment findFragment(View view) {
        for(ArticleDetailFragment f : mPagerFragments.values())
        {
            if(f.getView()==view)
                return f;
        }

        return null;
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mCurrentFragment = (ArticleDetailFragment) object;
        }

        @Override
        public Fragment getItem(int position) {
            mCursor.moveToPosition(position);
            // Todo: extract all value from cursor and pass to fragment to prevent a second loading

            //ArticleDetailFragment f = ArticleDetailFragment.newInstance(mCursor.getLong(ArticleLoader.Query._ID));
            ArticleDetailFragment f = ArticleDetailFragment.fromCursor(mCursor);
            // Keep a reference to the fragment
            mPagerFragments.put(position,f);

            return f;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            mPagerFragments.remove(position);
        }

        @Override
        public int getCount() {
            return (mCursor != null) ? mCursor.getCount() : 0;
        }
    }
}