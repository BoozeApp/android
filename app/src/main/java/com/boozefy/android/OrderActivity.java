package com.boozefy.android;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.boozefy.android.model.Order;
import com.boozefy.android.model.User;

import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private User user;
    private Order order;
    private long orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            orderId = getIntent().getLongExtra("orderId", -1);
        } else {
            orderId = savedInstanceState.getLong("orderId", -1);
        }

        if (orderId == -1) {
            finish();
            return;
        }

        user = User._.load(this);

        onRefresh();
    }

    @Override
    public void onRefresh() {
        Order.getService().get(orderId, user.getAccessToken()).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Response<Order> response) {
                order = response.body();

                if (user.getLevel() == User.LEVEL.client) {
                    drawForUser();
                } else {
                    drawForStaff();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                finish();
            }
        });
    }

    private void drawForUser() {

    }

    private void drawForStaff() {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("orderId", orderId);
    }
}
