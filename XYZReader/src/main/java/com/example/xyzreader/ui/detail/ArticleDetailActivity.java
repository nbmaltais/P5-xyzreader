package com.example.xyzreader.ui.detail;



import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
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
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.example.xyzreader.Utils;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.R;
import com.example.xyzreader.widget.FixedRatioImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */

public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ArticleDetailActivity.class.getSimpleName();
    private static final String EXTRA_TRANSITION_NAME = "TRANSITION_NAME";
    public static final String EXTRA_POSITION = "POSITION";
    public static final String EXTRA_ITEMID = "ITEMID";
    private Cursor mCursor;
    private int mPosition;
    private long mItemId;


    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;

    private HashMap<Integer,ArticleDetailFragment> mPagerFragments = new HashMap<>();
    private boolean mPostponed=false;
    private boolean mEntering=true;
    private ArticleDetailFragment mCurrentFragment;

    public static void start(Context ctx, int position, Uri uri, FixedRatioImageView thumbnailView) {
        Log.d(TAG,"Starting activity, uri = " + uri);
        Intent intent = new Intent(ctx,ArticleDetailActivity.class);
        intent.setData(uri);
        intent.putExtra(EXTRA_POSITION, position);

        if(thumbnailView!=null && ctx instanceof Activity)
        {
            String transitionName = ViewCompat.getTransitionName(thumbnailView);
            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) ctx, thumbnailView,transitionName ).toBundle();
            intent.putExtra(EXTRA_TRANSITION_NAME,transitionName);
            ctx.startActivity(intent,bundle);
        }
        else {

            ctx.startActivity(intent);
        }
    }


    /**
     * This code is inspired from https://github.com/alexjlockwood/activity-transitions
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    class MySharedElementCallback extends SharedElementCallback {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            // This callback is called both on entering and returning, so keep track
            if(mEntering) {
                mEntering=false;
                Log.d(TAG, "ENTERING ----------- onMapSharedElements-----------------");

                /*View v = mCurrentFragment.getContentScrollView();
                names.add(v.getTransitionName());
                sharedElements.put(v.getTransitionName(),v);*/
            }
            else
            {
                Log.d(TAG, "LEAVING ----------- onMapSharedElements-----------------");
                // When returning, we need to check if the view pager has changed. If so, we
                // remap the shared elements. this must also be done in the activity we return to
                if(mCurrentFragment !=null && mCurrentFragment.getDataId()!=mItemId)
                {
                    Log.d(TAG,"Remapping");
                    names.clear();
                    sharedElements.clear();

                    View sharedView = mCurrentFragment.getSharedImageView();
                    names.add(sharedView.getTransitionName());
                    sharedElements.put(sharedView.getTransitionName(), sharedView);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG,"onCreate");

        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // This is needed to remap the shared element if the view pager was used
            setEnterSharedElementCallback(new MySharedElementCallback());
        }

        if(savedInstanceState==null) {
            Log.d(TAG,"Postponing enter transition");
            mPostponed = true;
            ActivityCompat.postponeEnterTransition(this);

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
            // todo get position from bundle
        }


        setContentView(R.layout.activity_article_detail);

        getSupportLoaderManager().initLoader(0, null, this);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setPageTransformer(true, new Transformer());


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

        Log.d(TAG, "onCreate exit");

    }

    public void resumeContentTransitionAnimation( long id)
    {
        Log.d(TAG, "resumeContentTransitionAnimation, id = " + id + ", mItemId = " + mItemId);
        // Only resume the transitions if they wehre postponed and if the currently displayed fragment
        // is done loading
        if(mPostponed && id == mItemId)
        {
            Log.d(TAG, "Resuming!");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // All this is because the slide up content transition animation does not work
                // when we use the scroll view over an image background layout.
                // Instead , we queue the slide at the end of the shared transition animation
                // Is there an easier way??
                Transition.TransitionListener transitionListener = mCurrentFragment.getTransitionListener();
                if(transitionListener!=null)
                    getWindow().getSharedElementEnterTransition().addListener(transitionListener);
            }
            ActivityCompat.startPostponedEnterTransition(this);
        }
    }

    @Override
    public void supportFinishAfterTransition() {

        // Communicate back to the activity the current pager position so it can adjust the recycler view
        Intent data = new Intent();;
        data.putExtra(EXTRA_POSITION, mPosition);
        if(mCurrentFragment!=null)
            data.putExtra(EXTRA_ITEMID,mCurrentFragment.getDataId());
        setResult(RESULT_OK, data);

        super.supportFinishAfterTransition();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG,"onLoadFinished");
        mCursor = cursor;
        mPager.setAdapter(mPagerAdapter);
        //mPagerAdapter.notifyDataSetChanged();

        mPager.setCurrentItem(mPosition, false);
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
