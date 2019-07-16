package com.arif.gedor.DB;

/**
 * Created by Angga Kristiono on 16/11/2018.
 */

public class UserPdg {
    private String Fullname, Email, Password, ConfirmPass, Produk, Telepon;

    public UserPdg(){

    }

    public UserPdg(String fullname, String email, String password, String confirmPass, String produk, String telepon) {
        Fullname = fullname;
        Email = email;
        Password = password;
        ConfirmPass = confirmPass;
        Produk = produk;
        Telepon = telepon;
    }

    public String getFullname() {
        return Fullname;
    }

    public void setFullname(String fullname) {
        Fullname = fullname;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getConfirmPass() {
        return ConfirmPass;
    }

    public void setConfirmPass(String confirmPass) {
        ConfirmPass = confirmPass;
    }

    public String getProduk() {
        return Produk;
    }

    public void setProduk(String produk) {
        Produk = produk;
    }

    public String getTelepon() {
        return Telepon;
    }

    public void setTelepon(String telepon) {
        Telepon = telepon;
    }
}
