package com.boozefy.android.model;

import android.content.Context;

import com.boozefy.android.helper.ModelHelper;
import com.boozefy.android.helper.NetworkHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by mauricio on 5/30/16.
 */
public class Message {

    @DatabaseField(generatedId = false, id = true)
    private long id;
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private User sender;
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private User receiver;
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Order order;
    @DatabaseField
    private String message;
    @DatabaseField
    private String createdAt;

    @NetworkHelper.Exclude
    private static Service service;

    public interface Service {
        @GET("messages/{orderId}")
        Call<List<Message>> find(@Path("orderId") long orderId,
                                 @Query("access_token") String accessToken);
    }

    public Message() { }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public static void setService(Service service) {
        Message.service = service;
    }

    public static Service getService() {
        if (service == null) {
            service = NetworkHelper.getRetrofit().create(Service.class);
        }
        Collection<String> a = new ArrayList<String>();
        return service;
    }

    public static class _ {
        private static Dao<Message, Integer> messageDao = null;

        private static void loadDao(Context context) {
            if (messageDao == null) {
                messageDao = ModelHelper.getModelHelper(context).getMessageDao();
            }
        }

        public static void save(Message message, Context context) {
            loadDao(context);

            try {
                messageDao.createOrUpdate(message);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static Message get(long id, Context context) {
            loadDao(context);

            Message message = null;

            try {
                message = messageDao.queryBuilder().where().eq("id", id).queryForFirst();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return message;
        }
    }
}
