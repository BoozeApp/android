package com.boozefy.android;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.boozefy.android.adapter.CheckoutAdapter;
import com.boozefy.android.model.Booze;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

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
    private HashMap<Booze, Integer> selectedBoozesModel;
    private HashMap<Long, Integer> selectedBoozes;
    private CheckoutAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        ButterKnife.bind(this);
        
        setSupportActionBar(lToolbar);

        selectedBoozesModel = new HashMap<>();

        if (savedInstanceState == null) {
            selectedBoozes = (HashMap<Long, Integer>) getIntent().getSerializableExtra("selectedBoozes");
        } else {
            selectedBoozes = (HashMap<Long, Integer>) savedInstanceState.getSerializable("selectedBoozes");
        }

        Realm realm = Realm.getInstance(this);

        List<CheckoutAdapter.BoozeAmount> dataList = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : selectedBoozes.entrySet()) {
            Booze booze = realm.where(Booze.class).equalTo("id", entry.getKey()).findFirst();

            selectedBoozesModel.put(booze, entry.getValue());

            dataList.add(new CheckoutAdapter.BoozeAmount(booze, entry.getValue()));

            total += entry.getValue() * booze.getPrice();
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

                Snackbar.make(view, "Hey! I was just clicked!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putSerializable("selectedBoozes", selectedBoozes);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
