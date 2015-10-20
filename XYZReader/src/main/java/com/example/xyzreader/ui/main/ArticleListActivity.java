package com.example.xyzreader.ui.main;


import android.annotation.TargetApi;
import android.app.SharedElementCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.Utils;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.ui.detail.ArticleDetailActivity;
import com.example.xyzreader.widget.FixedRatioImageView;

import java.util.List;
import java.util.Map;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ArticleListActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private AppBarLayout mAppBarLayout;
    private ProgressBar mProgressBar;
    private boolean mIsReentering=false;
    private long mReenterItemId=-1;
    private RecyclerView mRecyclerView;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    class MySharedElementCallback extends SharedElementCallback
    {


        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            // This is called wehn exiting and reentering, so keep track
            Log.d(TAG,"**** onMapSharedElements");
            if(mIsReentering)
            {
                Log.d(TAG,"**** onMapSharedElements reentering==true");
                mIsReentering=false;
                // We are reentering from the detail activity. Check the parameter returned to decide if we
                // need to remap the shared elements.
                // This is taken from https://github.com/alexjlockwood/activity-transitions
                if(mReenterItemId!=-1) {
                    String newTransitionName = Utils.makeImageTransitionName(mReenterItemId);
                    View newSharedView = mRecyclerView.findViewWithTag(newTransitionName);
                    if (newSharedView != null) {
                        names.clear();
                        names.add(newTransitionName);
                        sharedElements.clear();
                        sharedElements.put(newTransitionName, newSharedView);
                    }
                }
            }
            else
            {
                Log.d(TAG,"**** onMapSharedElements reentering==false");
                /*String name = getString(R.string.transitionName_scrollView);
                names.add(name);
                sharedElements.put(name,null);*/

            }
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setExitSharedElementCallback(new MySharedElementCallback());
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mAppBarLayout = (AppBarLayout)findViewById(R.id.toolbar_container);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        //final View toolbarContainerView = findViewById(R.id.toolbar_container);


        //mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        getSupportLoaderManager().initLoader(0, null, this);

        if (savedInstanceState == null) {
            refresh();
        }
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {

        Log.d(TAG,"**** onActivityReenter");
        super.onActivityReenter(resultCode, data);

        mIsReentering=true;

        ActivityCompat.postponeEnterTransition(this);

        if(data!=null) {
            int position = data.getIntExtra(ArticleDetailActivity.EXTRA_POSITION, 0);
            mReenterItemId = data.getLongExtra(ArticleDetailActivity.EXTRA_ITEMID, -1);
            mRecyclerView.scrollToPosition(position);
            // Hide the app bar
            mAppBarLayout.setExpanded(false);
        }

        // This is taken from https://github.com/alexjlockwood/activity-transitions
        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                // TODO: hack! not sure why, but requesting a layout pass is necessary in order to fix re-mapping + scrolling glitches!
                mRecyclerView.requestLayout();
                ActivityCompat.startPostponedEnterTransition(ArticleListActivity.this);
                return true;
            }
        });
    }

    private void refresh() {
        startService(new Intent(this, UpdaterService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);
    }

    private boolean mIsRefreshing = false;

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                updateRefreshingUI();
            }
        }
    };

    private void updateRefreshingUI() {

        if(mIsRefreshing)
            mProgressBar.setVisibility(View.VISIBLE);
        else
            mProgressBar.setVisibility(View.GONE);
        //mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Adapter adapter = new Adapter( cursor);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }


}
