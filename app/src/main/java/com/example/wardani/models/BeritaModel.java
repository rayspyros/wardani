package com.example.wardani.models;

import java.io.Serializable;

public class BeritaModel implements Serializable {
    String judul;
    String waktu;
    String img_url;
    String deskripsi;

    public BeritaModel() {
    }

    public BeritaModel(String judul, String waktu, String img_url, String deskripsi) {
        this.judul = judul;
        this.waktu = waktu;
        this.img_url = img_url;
        this.deskripsi = deskripsi;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }
}
