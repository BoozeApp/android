package com.boozefy.android.model;

import android.content.Context;

import com.boozefy.android.helper.ModelHelper;
import com.boozefy.android.helper.NetworkHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Mauricio Giordano on 1/13/16.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Booze, 2016 - All rights reserved.
 */
@DatabaseTable
public class Order {

    @DatabaseField(generatedId = false, id = true)
    private long id;
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private User client;
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private User staff;
    @DatabaseField
    private double amount;
    @DatabaseField
    private double change;
    @DatabaseField
    private String status;
    @DatabaseField
    private String statusReason;
    @DatabaseField
    private String address;
    @DatabaseField
    private double latitude;
    @DatabaseField
    private double longitude;
    @NetworkHelper.Exclude
    @ForeignCollectionField(eager = true)
    private ForeignCollection<Beverage> beveragesRaw;
    private List<Beverage> beverages;

    @NetworkHelper.Exclude
    private static Service service;

    public interface Service {
        @GET("orders")
        Call<List<Order>> find();
    }

    public Order() { }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public User getStaff() {
        return staff;
    }

    public void setStaff(User staff) {
        this.staff = staff;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public List<Beverage> getBeverages() {
        if (beverages != null) return beverages;

        beverages = new ArrayList<>();

        for (Beverage beverage : beveragesRaw) {
            beverages.add(beverage);
        }

        return beverages;
    }

    public void setBeverages(List<Beverage> beverages) {
        this.beverages = beverages;
    }

    public void setBeverages(List<Beverage> beverages, Context context) {
        setBeverages(beverages);

        ModelHelper mh = ModelHelper.getModelHelper(context);
        Dao<Beverage, Integer> beverageDao = mh.getBeverageDao();

        try {
            this.beveragesRaw = beverageDao.getEmptyForeignCollection("beveragesRaw");
            this.beveragesRaw.addAll(beverages);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Service getService() {
        if (service == null) {
            service = NetworkHelper.getRetrofit().create(Service.class);
        }
        Collection<String> a = new ArrayList<String>();
        return service;
    }

    public static class _ {

    }
}

