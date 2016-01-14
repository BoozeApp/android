package com.boozefy.android.model;

import android.content.Context;

import com.boozefy.android.helper.NetworkHelper;
import com.boozefy.android.helper.RealmHelper;

import java.util.List;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Mauricio Giordano on 1/4/16.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Booze, 2016 - All rights reserved.
 */
public class Beverage extends RealmObject {

    @PrimaryKey
    private long id;
    private String name;
    private String picture;
    private double price;
    private int max;
    @Ignore
    private static Service service;

    public interface Service {
        @GET("beverages")
        Call<List<Beverage>> find();
    }

    public Beverage() { }

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

    public static Service getService() {
        if (service == null) {
            service = NetworkHelper.getRetrofit().create(Service.class);
        }

        return service;
    }

    public static class _ {

        public static List<Beverage> findById(Set<Long> ids, Context context) {
            Realm realm = RealmHelper.getInstance(context);
            RealmQuery<Beverage> where = realm.where(Beverage.class);

            for (Long id : ids) {
                where.or().equalTo("id", id);
            }

            return where.findAll();
        }

        public static List<Beverage> find(Context context) {
            Realm realm = RealmHelper.getInstance(context);
            return realm.allObjects(Beverage.class);
        }

        public static Beverage find(Long id, Context context) {
            Realm realm = RealmHelper.getInstance(context);
            return realm.where(Beverage.class).equalTo("id", id).findFirst();
        }

        public static void save(List<Beverage> beverages, Context context) {
            Realm realm = RealmHelper.getInstance(context);
            realm.beginTransaction();

            for (Beverage beverage : beverages) {
                realm.copyToRealmOrUpdate(beverage);
            }

            realm.commitTransaction();
        }

    }
}
