package com.example.wardani.admin.models;

public class KelolaPesananModel {
    private String Id;
    private String Customer;
    private String Detail;
    private String Harga;
    private String Nama;
    private String Order;
    private String Tanggal;
    private String Waktu;
    private String TglOrder;

    public KelolaPesananModel() {
    }

    public KelolaPesananModel(String id, String customer, String detail, String harga, String nama, String order, String tanggal, String waktu, String tgl_order) {
        Id = id;
        Customer = customer;
        Detail = detail;
        Harga = harga;
        Nama = nama;
        Order = order;
        Tanggal = tanggal;
        Waktu = waktu;
        TglOrder = tgl_order;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getCustomer() {
        return Customer;
    }

    public void setCustomer(String customer) {
        Customer = customer;
    }

    public String getDetail() {
        return Detail;
    }

    public void setDetail(String detail) {
        Detail = detail;
    }

    public String getHarga() {
        return Harga;
    }

    public void setHarga(String harga) {
        Harga = harga;
    }

    public String getNama() {
        return Nama;
    }

    public void setNama(String nama) {
        Nama = nama;
    }

    public String getOrder() {
        return Order;
    }

    public void setOrder(String order) {
        Order = order;
    }

    public String getTanggal() {
        return Tanggal;
    }

    public void setTanggal(String tanggal) {
        Tanggal = tanggal;
    }

    public String getWaktu() {
        return Waktu;
    }

    public void setWaktu(String waktu) {
        Waktu = waktu;
    }

    public String getTglOrder() {
        return TglOrder;
    }

    public void setTglOrder(String tgl_order) {
        TglOrder = tgl_order;
    }
}
