package com.boozefy.android.model;

import android.content.Context;

import com.boozefy.android.helper.ModelHelper;
import com.boozefy.android.helper.NetworkHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Mauricio Giordano on 1/4/16.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Booze, 2016 - All rights reserved.
 */
@DatabaseTable
public class Beverage {

    @DatabaseField(generatedId = false, id = true)
    private long id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String picture;
    @DatabaseField
    private double price;
    @DatabaseField
    private int max;

    @NetworkHelper.Exclude
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
        private static Dao<Beverage, Integer> beverageDao = null;

        private static void loadBeverageDao(Context context) {
            if (beverageDao == null) {
                beverageDao = ModelHelper.getModelHelper(context).getBeverageDao();
            }
        }

        public static List<Beverage> findById(List<Long> ids, Context context) {
            loadBeverageDao(context);

            List<Beverage> beverages = null;

            try {
                beverages = beverageDao.queryBuilder().where().in("id", ids).query();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return beverages;
        }

        public static List<Beverage> find(Context context) {
            loadBeverageDao(context);

            try {
                return beverageDao.queryForAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        public static Beverage find(Long id, Context context) {
            loadBeverageDao(context);

            try {
                return beverageDao.queryBuilder().where().eq("id", id).queryForFirst();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        public static void save(List<Beverage> beverages, Context context) {
            loadBeverageDao(context);

            for (Beverage beverage : beverages) {
                try {
                    beverageDao.createOrUpdate(beverage);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
