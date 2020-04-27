package com.example.masakuy.Feature.Beranda.Recyclerview;

//Todo: ini class buat nampung semua variabel yang dibutuhin di halaman item, misal ini BahanBakuModel, dia nampung semua variabel yang dibutuhin sama item pas milih bahan baku
public class BahanBakuModel {

    private String key,nama, jenis, deskripsi;
    private int stok, harga;

    public BahanBakuModel(String key,String nama, String jenis, String deskripsi, int stok, int harga) {
        this.key = key;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
