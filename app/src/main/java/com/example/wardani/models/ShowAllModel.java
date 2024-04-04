package com.example.wardani.models;

import java.io.Serializable;

public class ShowAllModel implements Serializable {

    String img_url;
    String nama_dalang;
    String deskripsi;
    int harga_jasa;
    Boolean tampilkan;

    public ShowAllModel() {
    }

    public ShowAllModel(String img_url, String nama_dalang, String deskripsi, int harga_jasa, Boolean tampilkan) {
        this.img_url = img_url;
        this.nama_dalang = nama_dalang;
        this.deskripsi = deskripsi;
        this.harga_jasa = harga_jasa;
        this.tampilkan = tampilkan;
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

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public int getHarga_jasa() {
        return harga_jasa;
    }

    public void setHarga_jasa(int harga_jasa) {
        this.harga_jasa = harga_jasa;
    }

    public Boolean getTampilkan() {
        return tampilkan;
    }

    public void setTampilkan(Boolean tampilkan) {
        this.tampilkan = tampilkan;
    }
}
