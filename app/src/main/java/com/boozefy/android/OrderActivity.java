package com.boozefy.android;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.boozefy.android.adapter.CheckoutAdapter;
import com.boozefy.android.model.Beverage;
import com.boozefy.android.model.Order;
import com.boozefy.android.model.User;
import com.boozefy.android.view.SwipeRefreshLayout;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.toolbar)
    public Toolbar lToolbar;
    @Bind(R.id.swipe_refresh)
    public SwipeRefreshLayout lSwipeRefreshLayout;
    @Bind(R.id.list_booze)
    public RecyclerView lListBooze;
    @Bind(R.id.layout_staff)
    public View lLayoutStaff;
    @Bind(R.id.layout_client)
    public View lLayoutClient;
    @Bind(R.id.text_total_price)
    public TextView lTextTotalPrice;
    @Bind(R.id.text_change)
    public TextView lTextChange;
    @Bind(R.id.text_address)
    public TextView lTextAddress;
    @Bind(R.id.button_deliver_this_order)
    public Button lButtonDeliverThisOrder;
    @Bind(R.id.button_send_message)
    public Button lButtonSendMessage;
    @Bind(R.id.button_send_message_client)
    public Button lButtonSendMessageClient;
    @Bind(R.id.button_call_client)
    public Button lButtonCallClient;
    @Bind(R.id.button_delivered)
    public Button lButtonDelivered;
    @Bind(R.id.text_total)
    public TextView lTextTotal;
    @Bind(R.id.text_status)
    public TextView lTextStatus;

    private User user;
    private Order order;
    private long orderId;
    private boolean afterCheckout;

    private CheckoutAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);

        setSupportActionBar(lToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            orderId = getIntent().getLongExtra("orderId", -1);
            afterCheckout = getIntent().getBooleanExtra("afterCheckout", false);
        } else {
            orderId = savedInstanceState.getLong("orderId", -1);
            afterCheckout = savedInstanceState.getBoolean("afterCheckout", false);
        }

        if (orderId == -1) {
            finish();
            return;
        }

        user = User._.load(this);

        adapter = new CheckoutAdapter();
        adapter.setDataList(new ArrayList<CheckoutAdapter.BoozeAmount>());
        lListBooze.setAdapter(adapter);
        lListBooze.setLayoutManager(new LinearLayoutManager(this));

        lSwipeRefreshLayout.setScrollView(lListBooze);
        lSwipeRefreshLayout.setOnRefreshListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onRefresh();
            }
        }, 150);
    }

    @Override
    public void onRefresh() {
        lSwipeRefreshLayout.setRefreshing(true);

        Order.getService().get(orderId, user.getAccessToken()).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Response<Order> response) {
                if (response.body() != null) {
                    order = response.body();

                    List<CheckoutAdapter.BoozeAmount> dataList = new ArrayList<CheckoutAdapter.BoozeAmount>();

                    for (Beverage beverage : order.getBeverages()) {
                        CheckoutAdapter.BoozeAmount boozeAmount = new CheckoutAdapter.BoozeAmount(
                                beverage,
                                beverage.getOrderBeverage().getAmount()
                        );

                        dataList.add(boozeAmount);
                    }

                    adapter.setDataList(dataList);

                    if (user.getLevel() == User.LEVEL.client) {
                        drawForUser();
                    } else {
                        drawForStaff();
                    }

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
                finish();
            }
        });
    }

    private void drawForUser() {
        lLayoutClient.setVisibility(View.VISIBLE);
        lLayoutStaff.setVisibility(View.GONE);

        lButtonSendMessageClient.setVisibility(View.GONE);
        lTextTotal.setText(String.format(Locale.ENGLISH, getString(R.string.text_price), order.getAmount()));

        switch (order.getStatus()) {
            case placed:
                lTextStatus.setText(R.string.text_processing);
                break;

            case in_transit:
                lTextStatus.setText(R.string.text_in_transit);

                lButtonSendMessageClient.setVisibility(View.VISIBLE);
                lButtonSendMessageClient.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                        intent.putExtra("orderId", orderId);
                        startActivity(intent);
                    }
                });
                break;

            case fulfilled:
                lTextStatus.setText(R.string.text_delivered);

            case rejected:
                lTextStatus.setText(R.string.text_rejected);
            break;
        }
    }

    private void drawForStaff() {
        lLayoutClient.setVisibility(View.GONE);
        lLayoutStaff.setVisibility(View.VISIBLE);

        lTextChange.setText(String.format(Locale.ENGLISH, getString(R.string.text_price), order.getChange()));
        lTextTotalPrice.setText(String.format(Locale.ENGLISH, getString(R.string.text_price), order.getAmount()));
        lTextAddress.setText(order.getAddress());

        lButtonSendMessage.setVisibility(View.GONE);
        lButtonCallClient.setVisibility(View.GONE);
        lButtonDelivered.setVisibility(View.GONE);

        if (order.getStatus() == Order.STATUS.in_transit) {
            lButtonDeliverThisOrder.setText(R.string.button_open_navigation);
            lButtonSendMessage.setVisibility(View.VISIBLE);
            lButtonCallClient.setVisibility(View.VISIBLE);
            lButtonDelivered.setVisibility(View.VISIBLE);
        } else if (order.getStatus() != Order.STATUS.placed) {
            lButtonDeliverThisOrder.setVisibility(View.GONE);
        }

        lButtonDeliverThisOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (order.getStatus() == Order.STATUS.in_transit) {
                    String uri = String.format(
                        Locale.ENGLISH,
                        "http://maps.google.com/maps?q=%s&z=%d",
                        order.getAddress(),
                        15
                    );

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);

                } else {
                    new AlertDialog.Builder(OrderActivity.this)
                        .setTitle(R.string.dialog_title_deliver_confirmation)
                        .setMessage(R.string.dialog_message_deliver_confirmation)
                        .setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deliverOrder();
                            }
                        })
                        .setNegativeButton(R.string.button_no, null)
                        .create()
                        .show();

                }
            }
        });

        lButtonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                intent.putExtra("orderId", orderId);
                startActivity(intent);
            }
        });

        lButtonCallClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + order.getClient().getTelephone()));

                startActivity(intent);
            }
        });

        lButtonDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(OrderActivity.this)
                    .setTitle(R.string.dialog_title_delivered)
                    .setMessage(getString(R.string.dialog_message_delivered))
                    .setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final ProgressDialog dialog = new ProgressDialog(OrderActivity.this);
                            dialog.setMessage(getString(R.string.dialog_loading_generic));
                            dialog.setIndeterminate(true);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();

                            Order.getService().fulfill(
                                    order.getId(),
                                    user.getAccessToken()
                            ).enqueue(new Callback<Order>() {
                                @Override
                                public void onResponse(Response<Order> response) {
                                    dialog.dismiss();

                                    if (response.body() != null) {
                                        order = response.body();

                                        lButtonDeliverThisOrder.setVisibility(View.GONE);
                                        lButtonSendMessage.setVisibility(View.GONE);
                                        lButtonCallClient.setVisibility(View.GONE);
                                        lButtonDelivered.setVisibility(View.GONE);

                                        Snackbar.make(lToolbar,
                                                R.string.snackbar_order_delivered,
                                                Snackbar.LENGTH_LONG).show();
                                    } else {
                                        Snackbar.make(lToolbar,
                                                R.string.snackbar_check_your_internet_connection,
                                                Snackbar.LENGTH_LONG).show();
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
                    })
                    .setNegativeButton(R.string.button_no, null)
                    .create()
                    .show();
            }
        });
    }

    private void deliverOrder() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.dialog_loading_generic));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Order.getService()
            .transit(order.getId(), user.getAccessToken())
            .enqueue(new Callback<Order>() {
                @Override
                public void onResponse(Response<Order> response) {
                    dialog.dismiss();

                    if (response.body() != null) {
                        lButtonDeliverThisOrder.setText(R.string.button_open_navigation);
                        order = response.body();

                        String uri = String.format(
                                Locale.ENGLISH,
                                "http://maps.google.com/maps?q=%s&z=%d",
                                order.getAddress(),
                                15
                        );

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);
                    } else {
                        Snackbar.make(lToolbar,
                                R.string.snackbar_check_your_internet_connection,
                                Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    dialog.dismiss();
                    Snackbar.make(lToolbar,
                            R.string.snackbar_check_your_internet_connection,
                            Snackbar.LENGTH_LONG).show();
                }
            }
        );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("orderId", orderId);
        outState.putBoolean("afterCheckout", afterCheckout);
    }

    @Override
    public void onBackPressed() {
        if (afterCheckout) {
            Intent intent = new Intent(this, AddressActivity.class);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
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
