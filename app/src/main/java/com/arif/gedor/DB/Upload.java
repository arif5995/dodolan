package com.arif.gedor.DB;

import com.google.firebase.database.Exclude;

/**
 * Created by Angga Kristiono on 20/11/2018.
 */

public class Upload {
    private String NamaProduk, UploadImage, Harga, Rincian, mKey;

    public Upload(){

    }

    public Upload(String namaProduk, String uploadImage, String harga, String rincian) {
        NamaProduk = namaProduk;
        UploadImage = uploadImage;
        Harga = harga;
        Rincian = rincian;
    }

    public String getNamaProduk() {
        return NamaProduk;
    }

    public void setNamaProduk(String namaProduk) {
        NamaProduk = namaProduk;
    }

    public String getUploadImage() {
        return UploadImage;
    }

    public void setUploadImage(String uploadImage) {
        UploadImage = uploadImage;
    }

    public String getHarga() {
        return Harga;
    }

    public void setHarga(String harga) {
        Harga = harga;
    }

    public String getRincian() {
        return Rincian;
    }

    public void setRincian(String rincian) {
        Rincian = rincian;
    }

    @Exclude
    public String getKey() {
        return mKey;
    }
    @Exclude
    public void setKey(String key) {
        mKey = key;
    }
}
