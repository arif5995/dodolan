package com.arif.gedor.remote;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Angga Kristiono on 31/10/2018.
 */

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseURL)
    {
        if(retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
