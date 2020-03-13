package com.example.masakuy.Feature.Beranda;

public class RecipeModel {
    private String nama_masakan;
    private String videoURL;

    public String getNama_masakan() {
        return nama_masakan;
    }

    public void setNama_masakan(String nama_masakan) {
        this.nama_masakan = nama_masakan;
    }

    public RecipeModel(String nama_masakan, String videoURL) {
        this.nama_masakan = nama_masakan;
        this.videoURL = videoURL;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }
}
