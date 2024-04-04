package com.example.wardani.models;

import com.google.firebase.Timestamp;

public class HistoryModel {
    String documentId;
    private String namaSeniman;
    private String hargaSeniman;
    private String nama;
    private String jalan;
    private String kota;
    private String provinsi;
    private String kodepos;
    private String telepon;
    private String tanggal;
    private String startTime;
    private String endTime;
    private Timestamp timeOrder;

    public HistoryModel() {
    }

    public HistoryModel(String documentId, String namaSeniman, String hargaSeniman, String nama, String jalan, String kota, String provinsi, String kodepos, String telepon, String tanggal, String startTime, String endTime, Timestamp timeOrder) {
        this.documentId = documentId;
        this.namaSeniman = namaSeniman;
        this.hargaSeniman = hargaSeniman;
        this.nama = nama;
        this.jalan = jalan;
        this.kota = kota;
        this.provinsi = provinsi;
        this.kodepos = kodepos;
        this.telepon = telepon;
        this.tanggal = tanggal;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeOrder = timeOrder;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getNamaSeniman() {
        return namaSeniman;
    }

    public void setNamaSeniman(String namaSeniman) {
        this.namaSeniman = namaSeniman;
    }

    public String getHargaSeniman() {
        return hargaSeniman;
    }

    public void setHargaSeniman(String hargaSeniman) {
        this.hargaSeniman = hargaSeniman;
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

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Timestamp getTimeOrder() {
        return timeOrder;
    }

    public void setTimeOrder(Timestamp timeOrder) {
        this.timeOrder = timeOrder;
    }
}
