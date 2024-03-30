package com.example.wardani.models;

import java.io.Serializable;
public class AddedModel implements Serializable {
    String documentId;
    String senimanNama;
    int senimanHarga;

    public AddedModel() {
    }

    public AddedModel(String documentId, String senimanNama, int senimanHarga) {
        this.documentId = documentId;
        this.senimanNama = senimanNama;
        this.senimanHarga = senimanHarga;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getSenimanNama() {
        return senimanNama;
    }

    public void setSenimanNama(String senimanNama) {
        this.senimanNama = senimanNama;
    }

    public int getSenimanHarga() {
        return senimanHarga;
    }

    public void setSenimanHarga(int senimanHarga) {
        this.senimanHarga = senimanHarga;
    }
}