package com.example.wardani.models;

public class AdminKelolaPesananModel {
    private String Id;
    private String Customer;
    private String Detail;
    private String Harga;
    private String Nama;
    private String Order;
    private String Tanggal;
    private String Waktu;

    public AdminKelolaPesananModel() {
    }

    public AdminKelolaPesananModel(String id, String customer, String detail, String harga, String nama, String order, String tanggal, String waktu) {
        Id = id;
        Customer = customer;
        Detail = detail;
        Harga = harga;
        Nama = nama;
        Order = order;
        Tanggal = tanggal;
        Waktu = waktu;
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
}
