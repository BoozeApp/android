package com.boozefy.android.model;

import android.content.Context;

import com.boozefy.android.helper.ModelHelper;
import com.boozefy.android.helper.NetworkHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;

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
@DatabaseTable
public class User {

    public enum LEVEL {
        client, staff, admin
    }

    @DatabaseField(generatedId = false, id = true)
    private long id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String email;
    @DatabaseField
    private String facebookId;
    @DatabaseField
    private String picture;
    @DatabaseField
    private LEVEL level;
    @DatabaseField
    private String accessToken;
    @DatabaseField
    private double latitude;
    @DatabaseField
    private double longitude;

    @NetworkHelper.Exclude
    private static Service service;

    public interface Service {
        @GET("users/me")
        Call<User> me(@Query("access_token") String accessToken);

        @FormUrlEncoded
        @POST("users/auth")
        Call<User> auth(@Field("fb_token") String fbToken,
                        @Field("device_type") String deviceType,
                        @Field("device_token") String deviceToken);
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

    public LEVEL getLevel() {
        return level;
    }

    public void setLevel(LEVEL level) {
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
        private static Dao<User, Integer> userDao = null;

        private static void loadUserDao(Context context) {
            if (userDao == null) {
                userDao = ModelHelper.getModelHelper(context).getUserDao();
            }
        }

        public static User load(Context context) {
            if (user != null) return user;

            loadUserDao(context);

            try {
                user = userDao.queryBuilder().where().ne("accessToken", "").queryForFirst();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return user;
        }

        public static void save(User user, Context context) {
            loadUserDao(context);

            try {
                userDao.createOrUpdate(user);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            _.user = user;
        }

        public static void remove(Context context) {
            loadUserDao(context);

            if (user != null) {
                try {
                    userDao.delete(user);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
