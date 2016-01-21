package com.boozefy.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.boozefy.android.adapter.BoozeSelectionAdapter;
import com.boozefy.android.helper.GeocoderHelper;
import com.boozefy.android.model.Beverage;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoozeSelectionActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    public Toolbar lToolbar;
    @Bind(R.id.list_booze)
    public RecyclerView lListBooze;
    @Bind(R.id.text_total)
    public TextView lTextTotal;
    @Bind(R.id.button_done)
    public Button lButtonDone;

    private BoozeSelectionAdapter adapterBooze;
    private List<Beverage> dataList;
    private GeocoderHelper.Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booze_selection);
        ButterKnife.bind(this);

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

        setSupportActionBar(lToolbar);

        adapterBooze = new BoozeSelectionAdapter();

        dataList = Beverage._.find(this);
        if (dataList == null) dataList = new ArrayList<>();

        adapterBooze.setDataList(dataList);
        adapterBooze.setOnTotalPriceChangedListener(new BoozeSelectionAdapter.OnTotalPriceChangedListener() {
            @Override
            public void onTotalPriceChanged(double newPrice) {
                lTextTotal.setText("Bs. " + newPrice);

                if (newPrice > 0) {
                    lButtonDone.setVisibility(View.VISIBLE);
                } else {
                    lButtonDone.setVisibility(View.INVISIBLE);
                }
            }
        });

        lListBooze.setLayoutManager(new LinearLayoutManager(this));
        lListBooze.setAdapter(adapterBooze);

        lButtonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BoozeSelectionActivity.this, CheckoutActivity.class);
                intent.putExtra("selectedBoozes", adapterBooze.getSelectedBoozes());
                intent.putExtra("location", location.toString());
                startActivity(intent);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Call<List<Beverage>> call = Beverage.getService().find();
        call.enqueue(new Callback<List<Beverage>>() {
            @Override
            public void onResponse(Response<List<Beverage>> response) {
                if (response.body() != null) {
                    List<Beverage> dataList = response.body();

                    Beverage._.save(dataList, BoozeSelectionActivity.this);

                    adapterBooze.setDataList(dataList);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("USER", "Err " + t.getMessage());
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
