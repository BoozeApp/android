package com.boozefy.android.model;

import android.content.Context;

import com.boozefy.android.helper.NetworkHelper;
import com.boozefy.android.helper.RealmHelper;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Mauricio Giordano on 1/13/16.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Booze, 2016 - All rights reserved.
 */
public class User extends RealmObject {

    enum LEVEL {
        client, staff, admin
    }

    @PrimaryKey
    private long id;
    private String name;
    private String email;
    private String facebookId;
    private String picture;
    private String level;
    private String accessToken;
    private double latitude;
    private double longitude;

    @Ignore
    private static Service service;

    public interface Service {
        @GET("users/me")
        Call<User> me(@Query("access_token") String accessToken);

        @FormUrlEncoded
        @POST("users/auth")
        Call<User> auth(@Field("fb_token") String fbToken);
    }

    public User() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public static Service getService() {
        if (service == null) {
            service = NetworkHelper.getRetrofit().create(Service.class);
        }

        return service;
    }

    public static class _ {
        private static User user = null;

        public static User load(Context context) {
            if (user != null) return user;

            Realm realm = RealmHelper.getInstance(context);
            RealmResults<User> results = realm.allObjects(User.class);

            if (results.size() > 0) {
                user = results.first();
            }

            return user;
        }

        public static void save(User user, Context context) {
            Realm realm = RealmHelper.getInstance(context);
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(user);
            realm.commitTransaction();

            _.user = user;
        }

        public static void remove(Context context) {
            Realm realm = RealmHelper.getInstance(context);
            realm.beginTransaction();
            realm.clear(User.class);
            realm.commitTransaction();
        }
    }
}
