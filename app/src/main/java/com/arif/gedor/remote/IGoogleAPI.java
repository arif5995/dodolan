package com.arif.gedor.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Angga Kristiono on 31/10/2018.
 */

public interface IGoogleAPI {
    @GET
    Call<String>getPath(@Url String url);
}
