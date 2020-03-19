package com.example.masakuy.Feature.Artikel.Recyclerview;

import android.os.Parcel;
import android.os.Parcelable;

public class ArtikelModel implements Parcelable {
    private String key, subject, body, imageURL;
    private int likeCount;

    public ArtikelModel(String key, String subject, String body, String imageURL, int likeCount) {
        this.key = key;
        this.subject = subject;
        this.body = body;
        this.imageURL = imageURL;
        this.likeCount = likeCount;
    }

    protected ArtikelModel(Parcel in) {
        key = in.readString();
        subject = in.readString();
        body = in.readString();
        imageURL = in.readString();
        likeCount = in.readInt();
    }

    public static final Creator<ArtikelModel> CREATOR = new Creator<ArtikelModel>() {
        @Override
        public ArtikelModel createFromParcel(Parcel in) {
            return new ArtikelModel(in);
        }

        @Override
        public ArtikelModel[] newArray(int size) {
            return new ArtikelModel[size];
        }
    };

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(key);
        parcel.writeString(subject);
        parcel.writeString(body);
        parcel.writeString(imageURL);
        parcel.writeInt(likeCount);
    }
}
