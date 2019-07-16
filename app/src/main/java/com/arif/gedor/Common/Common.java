package com.arif.gedor.Common;

import com.arif.gedor.remote.IGoogleAPI;
import com.arif.gedor.remote.RetrofitClient;


/**
 * Created by Angga Kristiono on 31/10/2018.
 */

public class Common {

    public static final String lokasiPdg_tbl = "Lokasi_Pedagang";
    public static final String UserPdg_tbl = "User_Pedagang";
    public static final String UserPbl_tbl = "User_Pembeli";
    public static final String LokasiPbl_tbl = "Lokasi_Pembeli";

    public static final String baseURL ="https://maps.googleapis.com";
    public static IGoogleAPI getGoogleAPI()
    {
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }
}
