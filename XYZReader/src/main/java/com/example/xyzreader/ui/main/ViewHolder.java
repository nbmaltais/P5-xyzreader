package com.example.xyzreader.ui.main;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.widget.FixedRatioImageView;

/**
 * Created by Nicolas on 2015-10-06.
 */
public class ViewHolder extends RecyclerView.ViewHolder {
    public FixedRatioImageView thumbnailView;
    public TextView titleView;
    public TextView subtitleView;

    public ViewHolder(View view) {
        super(view);
        thumbnailView = (FixedRatioImageView) view.findViewById(R.id.thumbnail);
        titleView = (TextView) view.findViewById(R.id.article_title);
        subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
    }
}
