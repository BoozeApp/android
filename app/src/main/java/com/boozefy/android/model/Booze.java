package com.boozefy.android.model;

import com.boozefy.android.helper.NetworkHelper;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Mauricio Giordano on 1/4/16.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Booze, 2016 - All rights reserved.
 */
public class Booze extends RealmObject {

    @PrimaryKey
    private long id;
    @Required
    private String name;
    private double price;
    @Required
    private String picture;
    private int max;
    @Ignore
    private Service service;

    public interface Service {
        @GET("booze")
        Call<List<Booze>> findBoozes();
    }

    public Booze() {
        service = NetworkHelper.getRetrofit().create(Service.class);
    }

    public Booze(long id, String name, double price, String picture) {
        this();

        this.id = id;
        this.name = name;
        this.price = price;
        this.picture = picture;
        this.max = 10;
    }

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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public Service getService() {
        return service;
    }
}
