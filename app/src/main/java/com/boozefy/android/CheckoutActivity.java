package com.boozefy.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.boozefy.android.adapter.CheckoutAdapter;
import com.boozefy.android.helper.GeocoderHelper;
import com.boozefy.android.model.Beverage;
import com.boozefy.android.model.Order;
import com.boozefy.android.model.User;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    public Toolbar lToolbar;
    @Bind(R.id.list_booze)
    public RecyclerView lRecyclerView;
    @Bind(R.id.text_total)
    public TextView lTextTotal;
    @Bind(R.id.edit_pay_amount)
    public EditText lEditPayAmount;
    @Bind(R.id.text_change)
    public TextView lTextChange;
    @Bind(R.id.button_cancel)
    public TextView lButtonCancel;
    @Bind(R.id.button_order)
    public TextView lButtonOrder;

    private double total = 0.0;
    private double payAmount = 0.0;
    private HashMap<Beverage, Integer> selectedBoozesModel;
    private HashMap<Long, Integer> selectedBoozes;
    private CheckoutAdapter adapter;

    private User user;
    private GeocoderHelper.Location location;
    private ProgressDialog progressOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        ButterKnife.bind(this);
        
        setSupportActionBar(lToolbar);

        if (savedInstanceState == null) {
            location = GeocoderHelper.Location.fromJson(
                    new JsonParser().parse(getIntent().getStringExtra("location")));
        } else {
            location = GeocoderHelper.Location.fromJson(
                    new JsonParser().parse(savedInstanceState.getString("location")));
        }

        if (location == null) {
            finish();
            return;
        }

        user = User._.load(this);
        selectedBoozesModel = new HashMap<>();

        if (savedInstanceState == null) {
            selectedBoozes = (HashMap<Long, Integer>) getIntent().getSerializableExtra("selectedBoozes");
        } else {
            selectedBoozes = (HashMap<Long, Integer>) savedInstanceState.getSerializable("selectedBoozes");
        }

        List<CheckoutAdapter.BoozeAmount> dataList = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : selectedBoozes.entrySet()) {
            Beverage beverage = Beverage._.find(entry.getKey(), this);

            selectedBoozesModel.put(beverage, entry.getValue());

            dataList.add(new CheckoutAdapter.BoozeAmount(beverage, entry.getValue()));

            total += entry.getValue() * beverage.getPrice();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new CheckoutAdapter();
        adapter.setDataList(dataList);
        lRecyclerView.setAdapter(adapter);
        lRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        lTextTotal.setText(String.valueOf(total));

        lEditPayAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                payAmount = 0.0;

                if (editable.toString().length() > 0) {
                    payAmount = Double.parseDouble(editable.toString());
                }

                if (payAmount >= total) {
                    lTextChange.setText("Bs. " + String.valueOf(payAmount - total));
                    lButtonOrder.setTextColor(ContextCompat.getColor(CheckoutActivity.this,
                                                                            R.color.colorAccent));
                    lTextChange.setTextColor(ContextCompat.getColor(CheckoutActivity.this,
                                                                            R.color.textPrice));
                } else {
                    lTextChange.setText("Bs. 0.00");
                    lTextChange.setTextColor(ContextCompat.getColor(CheckoutActivity.this,
                                                                            R.color.textError));
                    lButtonOrder.setTextColor(ContextCompat.getColor(CheckoutActivity.this,
                                                                            R.color.inactive));
                }
            }
        });

        lButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        lButtonOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (payAmount < total) return;

                progressOrder = new ProgressDialog(CheckoutActivity.this);
                progressOrder.setMessage("Creating order, please wait...");
                progressOrder.setIndeterminate(true);
                progressOrder.show();

                createOrder();
            }
        });
    }

    private void createOrder() {
        Call<Order> createCall = Order.getService().create(
                user.getAccessToken(),
                location.addressLine(),
                payAmount - total,
                location.latitude,
                location.longitude
        );

        createCall.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Response<Order> response) {
                if (response.body() != null) {
                    addBeverages(response.body());
                } else {
                    Log.d("ERROR", response.message());
                    progressOrder.dismiss();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("FAIL", t.getMessage());
                progressOrder.dismiss();
            }
        });
    }

    int beverageCounter = 0;

    private void addBeverages(final Order order) {
        for (Map.Entry<Beverage, Integer> entry : selectedBoozesModel.entrySet()) {
            Call<Order> addCall = Order.getService().addBeverage(
                order.getId(),
                entry.getKey().getId(),
                user.getAccessToken(),
                entry.getValue()
            );

            addCall.enqueue(new Callback<Order>() {
                @Override
                public void onResponse(Response<Order> response) {
                    if (response.body() != null) {
                        beverageCounter++;

                        if (beverageCounter == selectedBoozesModel.size()) {
                            placeOrder(order);
                        }
                    } else {
                        Log.d("ERROR", response.message());
                        progressOrder.dismiss();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("FAIL", t.getMessage());
                    progressOrder.dismiss();
                }
            });
        }
    }

    private void placeOrder(Order order) {
        Call<Order> placeCall = Order.getService().place(
            order.getId(),
            user.getAccessToken()
        );

        placeCall.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Response<Order> response) {
                if (response.body() != null) {
                    Intent intent = new Intent(CheckoutActivity.this, ThankYouActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d("ERROR", response.message());
                    progressOrder.dismiss();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("FAIL", t.getMessage());
                progressOrder.dismiss();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putSerializable("selectedBoozes", selectedBoozes);
        outState.putString("location", location.toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
