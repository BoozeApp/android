package com.boozefy.android.helper;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by Mauricio Giordano on 1/11/16.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Booze, 2016 - All rights reserved.
 */
public class NetworkHelper {
    private static final String baseURL = "http://localhost:3000/";//"http://booze.mauriciogior.com/";
    private static Retrofit retrofit = null;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            /*GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(User.class, new UserDeserializer());
            Gson gson = gsonBuilder.create();*/

            /*OkHttpClient httpClient = new OkHttpClient();
            httpClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Response response = chain.proceed(chain.request());

                    Log.d("NETWORKHELPER", "MESSAGE: [" + response.code() + "] " + response.message());

                    return response;
                }
            });*/

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setExclusionStrategies(new AnnotationExclusionStrategy());
            Gson gson = gsonBuilder.create();

            retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                //.client(httpClient)
                .build();
        }

        return retrofit;
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Exclude { }

    public static class AnnotationExclusionStrategy implements ExclusionStrategy {

        public boolean shouldSkipClass(Class<?> arg0) {
            return false;
        }

        public boolean shouldSkipField(FieldAttributes f) {
            return f.getAnnotation(Exclude.class) != null;
        }

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
