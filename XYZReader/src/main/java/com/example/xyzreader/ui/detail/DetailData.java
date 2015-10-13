package com.example.xyzreader.ui.detail;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spanned;

/**
 * Created by Nicolas on 2015-10-07.
 */
public class DetailData implements Parcelable {

    public String title;
    public long date;
    public String author;
    public String body;
    public String imageUrl;
    public String thumbUrl;
    public long id;

    public DetailData() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeLong(this.date);
        dest.writeString(this.author);
        dest.writeString(this.body);
        dest.writeString(this.imageUrl);
        dest.writeString(this.thumbUrl);
        dest.writeLong(this.id);
    }

    protected DetailData(Parcel in) {
        this.title = in.readString();
        this.date = in.readLong();
        this.author = in.readString();
        this.body = in.readString();
        this.imageUrl = in.readString();
        this.thumbUrl = in.readString();
        this.id = in.readLong();
    }

    public static final Creator<DetailData> CREATOR = new Creator<DetailData>() {
        public DetailData createFromParcel(Parcel source) {
            return new DetailData(source);
        }

        public DetailData[] newArray(int size) {
            return new DetailData[size];
        }
    };
}
