package com.example.masakuy.Feature.Beranda.Recyclerview;

import android.os.Parcel;
import android.os.Parcelable;

public class CourierModel implements Parcelable {

    private String key,nama,alamat,nohp;

    public CourierModel(String key, String nama, String alamat, String nohp) {
        this.key = key;
        this.nama = nama;
        this.alamat = alamat;
        this.nohp = nohp;
    }

    protected CourierModel(Parcel in) {
        key = in.readString();
        nama = in.readString();
        alamat = in.readString();
        nohp = in.readString();
    }

    public static final Creator<CourierModel> CREATOR = new Creator<CourierModel>() {
        @Override
        public CourierModel createFromParcel(Parcel in) {
            return new CourierModel(in);
        }

        @Override
        public CourierModel[] newArray(int size) {
            return new CourierModel[size];
        }
    };

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNohp() {
        return nohp;
    }

    public void setNohp(String nohp) {
        this.nohp = nohp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(nama);
        dest.writeString(alamat);
        dest.writeString(nohp);
    }
}
