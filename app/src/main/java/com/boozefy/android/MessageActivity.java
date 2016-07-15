package com.boozefy.android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.boozefy.android.adapter.MessageAdapter;
import com.boozefy.android.model.Message;
import com.boozefy.android.model.Order;
import com.boozefy.android.model.User;
import com.boozefy.android.view.SwipeRefreshLayout;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Date;
import java.util.List;

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
    @Bind(R.id.edit_message)
    public EditText lEditMessage;
    @Bind(R.id.button_send_message)
    public Button lButtonSendMessage;

    private long orderId;
    private MessageAdapter messageAdapter;
    private User user;
    private Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        ButterKnife.bind(this);

        setSupportActionBar(lToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        messageAdapter = new MessageAdapter(user);
        lListMessages.setLayoutManager(new LinearLayoutManager(this));
        lListMessages.setAdapter(messageAdapter);

        lSwipeRefreshLayout.setScrollView(lListMessages);
        lSwipeRefreshLayout.setOnRefreshListener(this);

        lButtonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lEditMessage.getText().toString().length() == 0) return;

                final ProgressDialog dialog = new ProgressDialog(MessageActivity.this);
                dialog.setMessage(getString(R.string.dialog_sending_message));
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                Order.getService().message(
                    orderId,
                    user.getAccessToken(),
                    lEditMessage.getText().toString()
                ).enqueue(new Callback<Order>() {
                    @Override
                    public void onResponse(Response<Order> response) {
                        dialog.dismiss();

                        if (response.body() != null) {
                            Snackbar.make(lToolbar,
                                    R.string.snackbar_message_sent,
                                    Snackbar.LENGTH_LONG).show();

                            Message message = new Message();
                            message.setId(-1);
                            message.setText(lEditMessage.getText().toString());
                            message.setSender(user);
                            message.setCreatedAt(String.valueOf(new Date().getTime()));

                            messageAdapter.addToDataList(message);

                            lEditMessage.setText("");
                        } else {
                            Snackbar.make(lToolbar,
                                    R.string.snackbar_check_your_internet_connection,
                                    Snackbar.LENGTH_LONG).show();

                            Log.d("createMessage", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        dialog.dismiss();

                        Snackbar.make(lToolbar,
                                R.string.snackbar_check_your_internet_connection,
                                Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onRefresh();

                handler.postDelayed(this, 3000);
            }
        }, 250);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong("orderId", orderId);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
