package com.example.wardani.admin.models;

public class KelolaCustomerModel {
    private String alamat;
    private String email;
    private String kodepos;
    private String kota;
    private String nama;
    private String provinsi;
    private String telepon;

    // Konstruktor kosong diperlukan untuk Firebase
    public KelolaCustomerModel() {
    }

    public KelolaCustomerModel(String alamat, String email, String kodepos, String kota, String nama, String provinsi, String telepon) {
        this.alamat = alamat;
        this.email = email;
        this.kodepos = kodepos;
        this.kota = kota;
        this.nama = nama;
        this.provinsi = provinsi;
        this.telepon = telepon;
    }

    // Getter dan Setter untuk setiap atribut
    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getKodepos() {
        return kodepos;
    }

    public void setKodepos(String kodepos) {
        this.kodepos = kodepos;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getProvinsi() {
        return provinsi;
    }

    public void setProvinsi(String provinsi) {
        this.provinsi = provinsi;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }
}

