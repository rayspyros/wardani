package com.example.wardani.models;

public class ContactUsModel {
    String DocumentId;
    String Alamat;

    public ContactUsModel() {
    }

    public ContactUsModel(String documentId, String alamat) {
        DocumentId = documentId;
        Alamat = alamat;
    }

    public String getDocumentId() {
        return DocumentId;
    }

    public void setDocumentId(String documentId) {
        DocumentId = documentId;
    }

    public String getAlamat() {
        return Alamat;
    }

    public void setAlamat(String alamat) {
        Alamat = alamat;
    }
}
