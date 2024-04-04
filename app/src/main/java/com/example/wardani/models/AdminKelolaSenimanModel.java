package com.example.wardani.models;

public class AdminKelolaSenimanModel {
    private String id;
    private String img_url;
    private String nama_dalang;
    private int harga_jasa;
    private String deskripsi;

    public AdminKelolaSenimanModel() {
    }

    public AdminKelolaSenimanModel(String id, String img_url, String nama_dalang, int harga_jasa, String deskripsi) {
        this.id = id;
        this.img_url = img_url;
        this.nama_dalang = nama_dalang;
        this.harga_jasa = harga_jasa;
        this.deskripsi = deskripsi;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getNama_dalang() {
        return nama_dalang;
    }

    public void setNama_dalang(String nama_dalang) {
        this.nama_dalang = nama_dalang;
    }

    public int getHarga_jasa() {
        return harga_jasa;
    }

    public void setHarga_jasa(int harga_jasa) {
        this.harga_jasa = harga_jasa;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }
}
