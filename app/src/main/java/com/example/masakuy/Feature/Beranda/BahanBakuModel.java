package com.example.masakuy.Feature.Beranda;

public class BahanBakuModel {

    private String nama, jenis, deskripsi;
    private int stok, harga;

    public BahanBakuModel(String nama, String jenis, String deskripsi, int stok, int harga) {
        this.nama = nama;
        this.jenis = jenis;
        this.deskripsi = deskripsi;
        this.stok = stok;
        this.harga = harga;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }
}
