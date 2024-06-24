package com.example.wardani.admin.models;

public class KelolaBeritaModel {
    private String id;
    private String img_url;
    private String judul;
    private String waktu;
    private String deskripsi;

    public KelolaBeritaModel() {
    }

    public KelolaBeritaModel(String id, String img_url, String judul, String waktu, String deskripsi) {
        this.id = id;
        this.img_url = img_url;
        this.judul = judul;
        this.waktu = waktu;
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

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }
}

