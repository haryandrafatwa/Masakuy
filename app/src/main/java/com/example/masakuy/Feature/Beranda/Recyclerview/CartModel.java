package com.example.masakuy.Feature.Beranda.Recyclerview;

public class CartModel {

    private String key,nama,jenis;
    private int amount, harga, berat;

    public CartModel(String key, String nama, String jenis, int berat, int amount, int harga) {
        this.key = key;
        this.nama = nama;
        this.jenis = jenis;
        this.berat = berat;
        this.amount = amount;
        this.harga = harga;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getBerat() {
        return berat;
    }

    public void setBerat(int berat) {
        this.berat = berat;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }
}
