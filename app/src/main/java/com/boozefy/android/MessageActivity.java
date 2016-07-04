package com.boozefy.android;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.boozefy.android.adapter.MessageAdapter;
import com.boozefy.android.model.Message;
import com.boozefy.android.model.User;
import com.boozefy.android.view.SwipeRefreshLayout;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mauricio on 5/30/16.
 */
public class MessageActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.toolbar)
    public Toolbar lToolbar;
    @Bind(R.id.swipe_refresh)
    public SwipeRefreshLayout lSwipeRefreshLayout;
    @Bind(R.id.list_messages)
    public RecyclerView lListMessages;

    private int orderId;
    private MessageAdapter messageAdapter;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        orderId = getIntent().getExtras().getInt("orderId", -1);

        if (orderId == -1) {
            orderId = savedInstanceState.getInt("orderId", -1);

            if (orderId == -1) {
                finish();
                return;
            }
        }

        ButterKnife.bind(this);

        user = User._.load(this);

        messageAdapter = new MessageAdapter(user);
        lListMessages.setLayoutManager(new LinearLayoutManager(this));
        lListMessages.setAdapter(messageAdapter);

        lSwipeRefreshLayout.setScrollView(lListMessages);
        lSwipeRefreshLayout.setOnRefreshListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onRefresh();
            }
        }, 250);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("orderId", orderId);
    }

    @Override
    public void onRefresh() {
        lSwipeRefreshLayout.setRefreshing(true);

        Message.getService().find(orderId, user.getAccessToken()).enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Response<List<Message>> response) {
                if (response.body() != null) {
                    List<Message> messageList = response.body();

                    messageAdapter.setDataList(messageList);

                    lSwipeRefreshLayout.setRefreshing(false);
                } else {
                    Snackbar.make(lToolbar,
                            R.string.snackbar_check_your_internet_connection,
                            Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Snackbar.make(lToolbar,
                        R.string.snackbar_check_your_internet_connection,
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
