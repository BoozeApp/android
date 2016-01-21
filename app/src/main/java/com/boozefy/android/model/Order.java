package com.boozefy.android.model;

import android.content.Context;

import com.boozefy.android.helper.ModelHelper;
import com.boozefy.android.helper.NetworkHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Mauricio Giordano on 1/13/16.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Booze, 2016 - All rights reserved.
 */
@DatabaseTable
public class Order {

    public enum STATUS {
        draft, placed, in_transit, fulfilled, rejected
    }

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
    private STATUS status;
    @DatabaseField
    private String statusReason;
    @DatabaseField
    private String address;
    @DatabaseField
    private double latitude;
    @DatabaseField
    private double longitude;
    @DatabaseField
    private String createdAt;

    private List<Beverage> beverages;

    @NetworkHelper.Exclude
    private static Service service;

    public interface Service {
        @GET("orders")
        Call<List<Order>> find(@Query("access_token") String accessToken);

        @GET("orders/{orderId}")
        Call<Order> get(@Path("orderId") long orderId,
                        @Query("access_token") String accessToken);

        @FormUrlEncoded
        @POST("orders")
        Call<Order> create(@Query("access_token") String accessToken,
                           @Field("address") String address,
                           @Field("change") double change,
                           @Field("latitude") double latitude,
                           @Field("longitude") double longitude);

        @FormUrlEncoded
        @POST("orders/{orderId}/beverages/{beverageId}")
        Call<Order> addBeverage(@Path("orderId") long orderId,
                                @Path("beverageId") long beverageId,
                                @Query("access_token") String accessToken,
                                @Field("amount") int amount);

        @PUT("orders/{orderId}/place")
        Call<Order> place(@Path("orderId") long orderId,
                          @Query("access_token") String accessToken);

        @PUT("orders/{orderId}/transit")
        Call<Order> transit(@Path("orderId") long orderId,
                            @Query("access_token") String accessToken);

        @PUT("orders/{orderId}/fulfill")
        Call<Order> fulfill(@Path("orderId") long orderId,
                            @Query("access_token") String accessToken);

        @PUT("orders/{orderId}/reject")
        Call<Order> reject(@Path("orderId") long orderId,
                          @Query("access_token") String accessToken);

        @GET("orders/placed")
        Call<List<Order>> placed(@Query("access_token") String accessToken);

        @GET("orders/in_transit")
        Call<List<Order>> inTransit(@Query("access_token") String accessToken);

        @GET("orders/fulfilled")
        Call<List<Order>> fulfilled(@Query("access_token") String accessToken);
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

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<Beverage> getBeverages() {
        return beverages;
    }

    public void setBeverages(List<Beverage> beverages) {
        this.beverages = beverages;
    }

    public static Service getService() {
        if (service == null) {
            service = NetworkHelper.getRetrofit().create(Service.class);
        }
        Collection<String> a = new ArrayList<String>();
        return service;
    }

    public static class _ {
        private static Dao<Order, Integer> orderDao = null;

        private static void loadOrderDao(Context context) {
            if (orderDao == null) {
                orderDao = ModelHelper.getModelHelper(context).getOrderDao();
            }
        }

        public static void save(Order order, Context context) {
            loadOrderDao(context);

            try {
                orderDao.createOrUpdate(order);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static Order get(long id, Context context) {
            loadOrderDao(context);

            Order order = null;

            try {
                order = orderDao.queryBuilder().where().eq("id", id).queryForFirst();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return order;
        }
    }
}

