package com.boozefy.android.helper;

import retrofit2.GsonConverterFactory;
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
            /*GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(User.class, new UserDeserializer());
            Gson gson = gsonBuilder.create();*/

            retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }

        return retrofit;
    }

    /*static class UserDeserializer implements JsonDeserializer<User> {
        @Override
        public User deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {

            JsonElement content = je.getAsJsonObject().get("data");

            return new Gson().fromJson(content, User.class);

        }
    }*/
}
