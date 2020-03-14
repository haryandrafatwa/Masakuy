package com.example.masakuy.Feature.Artikel.Recyclerview;

import android.os.Parcel;
import android.os.Parcelable;

public class ArtikelModel implements Parcelable {
    private String key, subject, body, imageURL;

    public ArtikelModel(String key, String subject, String body, String imageURL) {
        this.key = key;
        this.subject = subject;
        this.body = body;
        this.imageURL = imageURL;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
