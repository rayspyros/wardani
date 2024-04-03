package com.example.wardani.models;

public class AdminKelolaSenimanModel {
    private String img_url;
    private Integer harga_jasa;
    private String nama_dalang;
    private String deskripsi;

    public AdminKelolaSenimanModel() {
    }

    public AdminKelolaSenimanModel(String img_url, Integer harga_jasa, String nama_dalang, String deskripsi) {
        this.img_url = img_url;
        this.harga_jasa = harga_jasa;
        this.nama_dalang = nama_dalang;
        this.deskripsi = deskripsi;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public Integer getHarga_jasa() {
        return harga_jasa;
    }

    public void setHarga_jasa(Integer harga_jasa) {
        this.harga_jasa = harga_jasa;
    }

    public String getNama_dalang() {
        return nama_dalang;
    }

    public void setNama_dalang(String nama_dalang) {
        this.nama_dalang = nama_dalang;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }
}
