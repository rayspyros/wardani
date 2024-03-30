package com.example.wardani.models;

public class OrderModel {
    private String tanggal;
    private String waktuMulai;
    private String waktuSelesai;
    private String nama;
    private String jalan;
    private String kota;
    private String provinsi;
    private String kodepos;

    public OrderModel() {
    }

    public OrderModel(String tanggal, String waktuMulai, String waktuSelesai, String nama, String jalan, String kota, String provinsi, String kodepos) {
        this.tanggal = tanggal;
        this.waktuMulai = waktuMulai;
        this.waktuSelesai = waktuSelesai;
        this.nama = nama;
        this.jalan = jalan;
        this.kota = kota;
        this.provinsi = provinsi;
        this.kodepos = kodepos;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getWaktuMulai() {
        return waktuMulai;
    }

    public void setWaktuMulai(String waktuMulai) {
        this.waktuMulai = waktuMulai;
    }

    public String getWaktuSelesai() {
        return waktuSelesai;
    }

    public void setWaktuSelesai(String waktuSelesai) {
        this.waktuSelesai = waktuSelesai;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getJalan() {
        return jalan;
    }

    public void setJalan(String jalan) {
        this.jalan = jalan;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getProvinsi() {
        return provinsi;
    }

    public void setProvinsi(String provinsi) {
        this.provinsi = provinsi;
    }

    public String getKodepos() {
        return kodepos;
    }

    public void setKodepos(String kodepos) {
        this.kodepos = kodepos;
    }
}
