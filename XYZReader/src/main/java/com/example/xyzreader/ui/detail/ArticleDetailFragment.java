package com.example.xyzreader.ui.detail;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.xyzreader.Utils;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.R;
import com.example.xyzreader.ui.main.ArticleListActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ArticleDetailFragment";

    //public static final String ARG_ITEM_ID = "item_id";
    private static final String KEY_DATA = "data";

    private Cursor mCursor;
    private long mItemId;
    private View mRootView;
    private int mMutedColor = 0xFF333333;
    //private NestedScrollView mScrollView;
    //private ColorDrawable mStatusBarColorDrawable;

    private ImageView mPhotoView;

    private Toolbar mToolbar;
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    private DetailData mData;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    /*public static ArticleDetailFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }*/

    public static ArticleDetailFragment fromCursor(Cursor cursor) {
        DetailData data = new DetailData();

        data.title = cursor.getString(ArticleLoader.Query.TITLE);

        data.date = cursor.getLong(ArticleLoader.Query.PUBLISHED_DATE);
        data.author =  cursor.getString(ArticleLoader.Query.AUTHOR);
        data.body = cursor.getString(ArticleLoader.Query.BODY);
        data.imageUrl = cursor.getString(ArticleLoader.Query.PHOTO_URL);
        data.id = cursor.getLong(ArticleLoader.Query._ID);

        Bundle args = new Bundle();
        args.putParcelable(KEY_DATA, data);

        ArticleDetailFragment f = new ArticleDetailFragment();
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }*/
        if(getArguments().containsKey(KEY_DATA))
        {
            mData = getArguments().getParcelable(KEY_DATA);
        }

        //mIsCard = getResources().getBoolean(R.bool.detail_is_card);

        setHasOptionsMenu(true);
    }

    public ArticleDetailActivity getActivityCast() {
        return (ArticleDetailActivity) getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
        // the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
        // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
        // we do this in onActivityCreated.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        Log.d(TAG,"onCreateView itemid = " + mItemId);

        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);

        mToolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);

        adjustToolbarPadding(mToolbar);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) mRootView.findViewById(R.id.collapsing_toolbar);


        //mScrollView = (NestedScrollView) mRootView.findViewById(R.id.scrollview);

        mPhotoView = (ImageView) mRootView.findViewById(R.id.photo);

        //mStatusBarColorDrawable = new ColorDrawable(0);

        mRootView.findViewById(R.id.share_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
            }
        });

        //mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(supportActionBar!=null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
        }

        ViewCompat.setTransitionName(mPhotoView, "Image" + Long.toString(mItemId));
        bindViews();

       /*mRootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRootView.getViewTreeObserver().removeOnPreDrawListener(this);
                ArticleDetailActivity a = getActivityCast();
                if(a!=null) {
                    a.resumeContentTransitionAnimation();
                }
                return true;
            }
        });*/

        return mRootView;
    }

    private void adjustToolbarPadding(Toolbar toolbar) {
        int padding =  Utils.getStatusBarHeight(getActivity());
        ViewGroup.LayoutParams layoutParams = toolbar.getLayoutParams();
        layoutParams.height += padding;
        toolbar.setLayoutParams(layoutParams);
        toolbar.setPadding(0, padding, 0, 0);
    }



    static float constrain(float val, float min, float max) {
        if (val < min) {
            return min;
        } else if (val > max) {
            return max;
        } else {
            return val;
        }
    }

    private void bindViews() {
        if (mRootView == null) {
            return;
        }

        TextView titleView = (TextView) mRootView.findViewById(R.id.article_title);
        TextView bylineView = (TextView) mRootView.findViewById(R.id.article_byline);
        bylineView.setMovementMethod(new LinkMovementMethod());
        TextView bodyView = (TextView) mRootView.findViewById(R.id.article_body);
        bodyView.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Rosario-Regular.ttf"));


        if (mCursor != null) {
            mRootView.setVisibility(View.VISIBLE);
            /*mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);*/

            String title = mCursor.getString(ArticleLoader.Query.TITLE);
            Spanned byline = Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + " by <font color='#ffffff'>"
                            + mCursor.getString(ArticleLoader.Query.AUTHOR)
                            + "</font>");

            if(mCollapsingToolbarLayout!=null)
                mCollapsingToolbarLayout.setTitle(title);


            titleView.setText(title);
            bylineView.setText(byline);
            bodyView.setText(Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY)));

            String imageUrl = mCursor.getString(ArticleLoader.Query.PHOTO_URL);
            Picasso.with(getActivity()).load(imageUrl).into(mPhotoView, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG,"Image loaded, itemid = " + mItemId);
                    onImageLoaded();
                }

                @Override
                public void onError() {
                    onImageLoaded();
                }
            });

            long id = mCursor.getLong(ArticleLoader.Query._ID);


            // TODO: generate palette
            /*ImageLoaderHelper.getInstance(getActivity()).getImageLoader()
                    .get(imageUrl, new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                            Bitmap bitmap = imageContainer.getBitmap();
                            if (bitmap != null) {
                                Palette p = Palette.generate(bitmap, 12);
                                mMutedColor = p.getDarkMutedColor(0xFF333333);
                                mPhotoView.setImageBitmap(imageContainer.getBitmap());
                                mRootView.findViewById(R.id.meta_bar)
                                        .setBackgroundColor(mMutedColor);
                                updateStatusBar();
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }
                    });*/
        } else {
            //mRootView.setVisibility(View.GONE);
            titleView.setText("N/A");
            bylineView.setText("N/A");
            bodyView.setText("N/A");
        }
    }

    /**
     * Called after image load either failed of succeed
     */
    private void onImageLoaded() {
        Log.d(TAG, "onImageLoaded, item id = mItemId");
        getActivityCast().resumeContentTransitionAnimation(mItemId);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newInstanceForItemId(getActivity(), mItemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }

        bindViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        bindViews();
    }


    public void transformPage(View page, float position) {
        int pageWidth = page.getWidth();

        if(mToolbar!=null)
        {
            if(position < -1 || position > 1)
            {
                mToolbar.setTranslationX(0);
            }
            else {
                // Counteract the default slide transition
                mToolbar.setTranslationX(pageWidth * -position);
            }
        }
        // Add a parralax effect when swiping between fragment
        if(mPhotoView!=null)
        {
            if(position < -1 || position > 1) {
                mPhotoView.setTranslationX(0);
            }
            else
            {
                mPhotoView.setTranslationX(pageWidth * -position / 2);
            }
        }
    }


}