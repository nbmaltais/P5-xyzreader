package com.example.xyzreader.ui.main;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.ui.detail.ArticleDetailActivity;
import com.squareup.picasso.Picasso;

/**
 * Created by Nicolas on 2015-10-06.
 */
class Adapter extends RecyclerView.Adapter<ViewHolder> {
    //private ArticleListActivity mArticleListActivity;
    private Cursor mCursor;

    public Adapter( Cursor cursor) {
        mCursor = cursor;
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(ArticleLoader.Query._ID);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final Context ctx = parent.getContext();

        View view = LayoutInflater.from(ctx).inflate(R.layout.list_item_article, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()));
                ArticleDetailActivity.start(ctx,vh.getAdapterPosition(),uri,vh.thumbnailView);

                /*ctx.startActivity(new Intent(Intent.ACTION_VIEW,
                        ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()))));*/
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Context ctx = holder.itemView.getContext();

        mCursor.moveToPosition(position);
        holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
        // TODO: use resources
        long publishedDate = mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE);
        String date = DateUtils.getRelativeTimeSpanString(
                publishedDate,
                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL).toString();
        String author = mCursor.getString(ArticleLoader.Query.AUTHOR);
        holder.subtitleView.setText(ctx.getString(R.string.article_subtitle, date, author));

        String imageUrl = mCursor.getString(ArticleLoader.Query.THUMB_URL);

        holder.thumbnailView.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));

        ViewCompat.setTransitionName(holder.thumbnailView, "Image" + Long.toString(getItemId(position)) );

        // TODO: add place holder
        Picasso.with(ctx).load(imageUrl).into(holder.thumbnailView);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }
}
