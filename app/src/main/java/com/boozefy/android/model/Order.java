package com.boozefy.android.model;

import com.boozefy.android.helper.NetworkHelper;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Mauricio Giordano on 1/13/16.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Booze, 2016 - All rights reserved.
 */
public class Order extends RealmObject {

    @PrimaryKey
    private long id;
    private User client;
    private User staff;
    private double amount;
    private double change;
    private String status;
    private String statusReason;
    private String address;
    private double latitude;
    private double longitude;
    private RealmList<Beverage> beverages;

    @Ignore
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

    public RealmList<Beverage> getBeverages() {
        return beverages;
    }

    public void setBeverages(RealmList<Beverage> beverages) {
        this.beverages = beverages;
    }

    public static Service getService() {
        if (service == null) {
            service = NetworkHelper.getRetrofit().create(Service.class);
        }

        return service;
    }

    public static class _ {

    }
}

