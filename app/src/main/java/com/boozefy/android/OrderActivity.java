package com.boozefy.android;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.boozefy.android.adapter.CheckoutAdapter;
import com.boozefy.android.model.Beverage;
import com.boozefy.android.model.Order;
import com.boozefy.android.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.toolbar)
    public Toolbar lToolbar;
    @Bind(R.id.list_booze)
    public RecyclerView lListBooze;
    @Bind(R.id.layout_staff)
    public View lLayoutStaff;
    @Bind(R.id.layout_client)
    public View lLayoutClient;
    @Bind(R.id.text_change)
    public TextView lTextChange;
    @Bind(R.id.text_address)
    public TextView lTextAddress;
    @Bind(R.id.button_deliver_this_order)
    public Button lButtonDeliverThisOrder;
    @Bind(R.id.text_total)
    public TextView lTextTotal;
    @Bind(R.id.text_status)
    public TextView lTextStatus;

    private User user;
    private Order order;
    private long orderId;

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
        } else {
            orderId = savedInstanceState.getLong("orderId", -1);
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

        onRefresh();
    }

    @Override
    public void onRefresh() {
        Order.getService().get(orderId, user.getAccessToken()).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Response<Order> response) {
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
            }

            @Override
            public void onFailure(Throwable t) {
                finish();
            }
        });
    }

    private void drawForUser() {
        lLayoutClient.setVisibility(View.VISIBLE);
        lLayoutStaff.setVisibility(View.GONE);


    }

    private void drawForStaff() {
        lLayoutClient.setVisibility(View.GONE);
        lLayoutStaff.setVisibility(View.VISIBLE);

        lTextChange.setText(String.format(Locale.ENGLISH, getString(R.string.text_price), order.getChange()));
        lTextAddress.setText(order.getAddress());

        if (order.getStatus() == Order.STATUS.in_transit) {
            lButtonDeliverThisOrder.setText(R.string.button_open_navigation);
        } else if (order.getStatus() != Order.STATUS.placed) {
            lButtonDeliverThisOrder.setVisibility(View.GONE);
        }

        lButtonDeliverThisOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (order.getStatus() == Order.STATUS.in_transit) {
                    // Open GPS
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

                        // Open GPS
                    } else {

                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    dialog.dismiss();
                }
            }
        );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("orderId", orderId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
