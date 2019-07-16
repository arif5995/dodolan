package com.arif.gedor.DB;

/**
 * Created by Angga Kristiono on 16/11/2018.
 */

public class UserPbl {
    private String Fullname, Email, Password, ConfirmPass, Telepon;

    public UserPbl(){

    }

    public UserPbl(String fullname, String email, String password, String confirmPass, String telepon) {
        Fullname = fullname;
        Email = email;
        Password = password;
        ConfirmPass = confirmPass;
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

    public String getTelepon() {
        return Telepon;
    }

    public void setTelepon(String telepon) {
        Telepon = telepon;
    }
}
