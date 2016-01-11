package com.boozefy.android.helper;

import retrofit2.Retrofit;

/**
 * Created by Mauricio Giordano on 1/11/16.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Booze, 2016 - All rights reserved.
 */
public class NetworkHelper {
    private static final String baseURL = "http://10.0.3.2:3000/";
    private static Retrofit retrofit = null;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .build();
        }

        return retrofit;
    }
}
