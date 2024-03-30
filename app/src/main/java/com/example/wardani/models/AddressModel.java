package com.example.wardani.models;

public class AddressModel {

    String userAlamat;
    boolean isSelected;

    public AddressModel() {
    }

    public AddressModel(String userAlamat, boolean isSelected) {
        this.userAlamat = userAlamat;
        this.isSelected = isSelected;
    }

    public String getUserAlamat() {
        return userAlamat;
    }

    public void setUserAlamat(String userAlamat) {
        this.userAlamat = userAlamat;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
