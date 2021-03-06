package com.example.masakuy.Feature.Beranda.Recyclerview;

import android.os.Parcel;
import android.os.Parcelable;

public class RecipeModel implements Parcelable {
    private String key, nama_masakan, bahan, cara_masak, oleh, email, deskripsi,imageURL, videoURL;
    private int lama_masak,likeCount;

    public RecipeModel(String key, String nama_masakan, String bahan, String cara_masak, int lama_masak, String oleh, String email, String imageURL, String videoURL, String deskripsi, int likeCount) {
        this.key = key;
        this.nama_masakan = nama_masakan;
        this.bahan = bahan;
        this.cara_masak = cara_masak;
        this.lama_masak = lama_masak;
        this.oleh = oleh;
        this.email = email;
        this.imageURL = imageURL;
        this.videoURL = videoURL;
        this.deskripsi = deskripsi;
        this.likeCount = likeCount;
    }

    protected RecipeModel(Parcel in) {
        key = in.readString();
        nama_masakan = in.readString();
        bahan = in.readString();
        cara_masak = in.readString();
        oleh = in.readString();
        email = in.readString();
        lama_masak = in.readInt();
        videoURL = in.readString();
        imageURL = in.readString();
        deskripsi = in.readString();
        likeCount = in.readInt();
    }

    public static final Creator<RecipeModel> CREATOR = new Creator<RecipeModel>() {
        @Override
        public RecipeModel createFromParcel(Parcel in) {
            return new RecipeModel(in);
        }

        @Override
        public RecipeModel[] newArray(int size) {
            return new RecipeModel[size];
        }
    };

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNama_masakan() {
        return nama_masakan;
    }

    public void setNama_masakan(String nama_masakan) {
        this.nama_masakan = nama_masakan;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getBahan() {
        return bahan;
    }

    public void setBahan(String bahan) {
        this.bahan = bahan;
    }

    public String getCara_masak() {
        return cara_masak;
    }

    public void setCara_masak(String cara_masak) {
        this.cara_masak = cara_masak;
    }

    public int getLama_masak() {
        return lama_masak;
    }

    public void setLama_masak(int lama_masak) {
        this.lama_masak = lama_masak;
    }

    public String getOleh() {
        return oleh;
    }

    public void setOleh(String oleh) {
        this.oleh = oleh;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        parcel.writeString(nama_masakan);
        parcel.writeString(bahan);
        parcel.writeString(cara_masak);
        parcel.writeString(oleh);
        parcel.writeString(email);
        parcel.writeString(deskripsi);
        parcel.writeInt(lama_masak);
        parcel.writeString(imageURL);
        parcel.writeString(videoURL);
        parcel.writeInt(likeCount);
    }
}
