package com.example.xyzreader.ui.detail;


import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.transition.Transition;
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
import com.example.xyzreader.utility.ColorResolver;
import com.example.xyzreader.utility.DarkMutedColorResolver;
import com.example.xyzreader.utility.DominantColorResolver;
import com.example.xyzreader.utility.PaletteTransformation;
import com.example.xyzreader.ui.main.ArticleListActivity;
import com.squareup.picasso.Picasso;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment {
    private static final String TAG = "ArticleDetailFragment";

    //public static final String ARG_ITEM_ID = "item_id";
    private static final String KEY_DATA = "data";

    private View mRootView;


    private ImageView mPhotoView;

    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    private DetailData mData;
    private TextView mTitleView;
    private TextView mBylineView;
    private TextView mBodyView;
    private View mScrollview;
    private FloatingActionButton mFab;
    private View mMetaBar;
    private boolean mImageLoaded = false;
    private boolean mResumeOnImageLoad = false;
    private boolean mIsCard=false;
    private int mMutedColor;
    private int mAccentColor;
    private View mSharedImageView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }


    public static ArticleDetailFragment fromCursor(Cursor cursor) {
        DetailData data = new DetailData();

        data.title = cursor.getString(ArticleLoader.Query.TITLE);

        data.date = cursor.getLong(ArticleLoader.Query.PUBLISHED_DATE);
        data.author = cursor.getString(ArticleLoader.Query.AUTHOR);
        data.body = cursor.getString(ArticleLoader.Query.BODY);
        data.imageUrl = cursor.getString(ArticleLoader.Query.PHOTO_URL);
        data.thumbUrl = cursor.getString(ArticleLoader.Query.THUMB_URL);
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
        if (getArguments().containsKey(KEY_DATA)) {
            mData = getArguments().getParcelable(KEY_DATA);
        }

        setHasOptionsMenu(true);
    }

    public ArticleDetailActivity getActivityCast() {
        return (ArticleDetailActivity) getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView itemid = " + mData.id);

        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);

        mIsCard = getResources().getBoolean(R.bool.is_card);

        mToolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

        adjustToolbarPadding(mToolbar);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) mRootView.findViewById(R.id.collapsing_toolbar);
        mPhotoView = (ImageView) mRootView.findViewById(R.id.photo);
        mTitleView = (TextView) mRootView.findViewById(R.id.article_title);
        mBylineView = (TextView) mRootView.findViewById(R.id.article_byline);
        mBodyView = (TextView) mRootView.findViewById(R.id.article_body);
        mMetaBar = mRootView.findViewById(R.id.meta_bar);
        mFab = (FloatingActionButton)mRootView.findViewById(R.id.share_fab);
        mScrollview = mRootView.findViewById(R.id.scrollview);

        mFab.setOnClickListener(new View.OnClickListener() {
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
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
        }

        ViewCompat.setTransitionName(mPhotoView, Utils.makeImageTransitionName(mData.id));
        bindViews();

        //mImageLoaded=true;
        mRootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                //Log.d(TAG, "mRootView.getViewTreeObserver().onPreDraw, itemId = " + mData.id);
                mRootView.getViewTreeObserver().removeOnPreDrawListener(this);
                ArticleDetailActivity a = getActivityCast();
                if (a != null /*&& mImageLoaded*/) {
                    a.resumeContentTransitionAnimation(mData.id);
                } /*else {
                    mResumeOnImageLoad = true;
                }*/
                return true;
            }
        });

        mMutedColor = getResources().getColor(R.color.primary);
        mAccentColor = getResources().getColor(R.color.accent);


        Log.d(TAG, "onCreateView exit");
        return mRootView;
    }



    private void adjustToolbarPadding(Toolbar toolbar) {
        int padding = Utils.getStatusBarHeight(getActivity());
        ViewGroup.LayoutParams layoutParams = toolbar.getLayoutParams();
        layoutParams.height += padding;
        toolbar.setLayoutParams(layoutParams);
        toolbar.setPadding(0, padding, 0, 0);
    }


    private void bindViews() {
        if (mRootView == null) {
            return;
        }


        mBylineView.setMovementMethod(new LinkMovementMethod());

        mBodyView.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Rosario-Regular.ttf"));


        if (mData != null) {
            //mRootView.setVisibility(View.VISIBLE);
            /*mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);*/

            String title = mData.title;
            Spanned byline = Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            mData.date,
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + " by <font color='#ffffff'>"
                            + mData.author
                            + "</font>");

            if (mCollapsingToolbarLayout != null)
                mCollapsingToolbarLayout.setTitle(title);


            mTitleView.setText(title);
            mBylineView.setText(byline);
            mBodyView.setText(Html.fromHtml(mData.body));



            Picasso.with(getActivity()).load(mData.imageUrl)
                    .fit()
                    .centerCrop()
                    .noFade()
                    //.placeholder(R.drawable.image_placeholder)
                    .transform(PaletteTransformation.instance())
                    .into(mPhotoView, new PaletteTransformation.PaletteCallback(mPhotoView) {
                        @Override
                        public void onError() {

                            onImageLoaded();
                        }

                        @Override
                        protected void onSuccess(Palette palette) {
                            Log.d(TAG, "Image loaded, itemid = " + mData.id);
                            onImageLoaded();
                            applyPalette(palette);

                            // Good! now load high res version
                            /*Picasso.with(getActivity()).load(mData.imageUrl)
                                    .fit()
                                    .centerCrop()
                                    .transform(PaletteTransformation.instance())
                                    .into(mPhotoView);*/
                        }
                    });


        } else {
            //mRootView.setVisibility(View.GONE);
            mTitleView.setText("N/A");
            mBylineView.setText("N/A");
            mBodyView.setText("N/A");
        }
    }

    private void applyPalette(Palette palette) {

        if(getActivity()==null)
            return;

        ColorResolver resolver;

        if(mIsCard) {
            resolver = new DarkMutedColorResolver(palette);

            if (!resolver.isResolved())
                resolver = new DominantColorResolver(palette);
            if (!resolver.isResolved())
                return;
        }
        else
        {
            resolver = new DominantColorResolver(palette);

            if (!resolver.isResolved())
                resolver = new DarkMutedColorResolver(palette);
            if (!resolver.isResolved())
                return;
        }
        long duration=300;
        mMutedColor = resolver.getPrimarySwatch().getRgb();
        mAccentColor = resolver.getAccentSwatch().getRgb();

        int primary = getResources().getColor(R.color.primary);
        int accent = getResources().getColor(R.color.accent);
        if(mMetaBar!=null) {

            int colorFrom = Utils.getBackgroundColor(mMetaBar,primary);
            //mMetaBar.setBackgroundColor(mMutedColor);
            Utils.animateBackgroundColor(mMetaBar,colorFrom,mMutedColor,duration);
        }
        if(mFab!=null) {
            int colorFrom = mFab.getBackgroundTintList().getDefaultColor();
            //mFab.setBackgroundTintList(ColorStateList.valueOf(mAccentColor));
            Utils.animateBackgroundTintList(mFab,colorFrom,mAccentColor,duration);
        }
        if(mCollapsingToolbarLayout!=null) {
            mCollapsingToolbarLayout.setContentScrimColor(mMutedColor);
        }
    }

    /**
     * Called after image load either failed of succeed
     */
    private void onImageLoaded() {
        mImageLoaded=true;
        Log.d(TAG, "onImageLoaded, item id = " + mData.id);
        /*ArticleDetailActivity a = getActivityCast();
        if (a != null && mResumeOnImageLoad) {
            //a.resumeContentTransitionAnimation(mPhotoView, mData.id);
            a.resumeContentTransitionAnimation(mData.id);
        }*/

    }


    public void transformPage(View page, float position) {
        int pageWidth = page.getWidth();

        if (mToolbar != null) {
            if (position < -1 || position > 1) {
                mToolbar.setTranslationX(0);
            } else {
                // Counteract the default slide transition
                mToolbar.setTranslationX(pageWidth * -position);
            }
        }
        // Add a parralax effect when swiping between fragment
        if (mPhotoView != null) {
            if (position < -1 || position > 1) {
                mPhotoView.setTranslationX(0);
            } else {
                mPhotoView.setTranslationX(pageWidth * -position * .5f);
            }
        }



        if (mBodyView != null) {
            /*if (position < -1 || position > 1) {
                mBodyView.setTranslationX(0);
            } else {
                mBodyView.setTranslationX(pageWidth * -position * .5f);
            }*/
            float a = position;
            if(a<0)a=-1;
            mBodyView.setAlpha(1 - a);
        }


    }

    public long getDataId()
    {
        return mData.id;
    }

    public View getSharedImageView() {
        return mPhotoView;
    }

    public View getContentScrollView() { return mScrollview;}

    /**
     * This method is called from the parent activity to set an initial image.
     * This is the snapshot image acquired from the list activity. It will be replaced by the
     * high res image when it is loaded.
     * @param snapshotDrawable
     */
    public void setSharedImageViewSnapshotImage(Drawable snapshotDrawable) {
        // If high res image is already loaded, don't change it!
        if(mImageLoaded==false)
        {
            mPhotoView.setImageDrawable(snapshotDrawable);
        }
    }
}
