package com.boozefy.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.boozefy.android.adapter.BoozeSelectionAdapter;
import com.boozefy.android.model.Booze;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

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
    private List<Booze> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booze_selection);
        ButterKnife.bind(this);

        setSupportActionBar(lToolbar);

        adapterBooze = new BoozeSelectionAdapter();

        dataList = new ArrayList<>();
        dataList.add(new Booze(1, "Vodka", 150.00, "http://iacom1-a.akamaihd.net/produtos/01/00/item/5432/2/5432285_1GG.jpg"));
        dataList.add(new Booze(2, "Rum + Coke", 100.00, "http://liquor.s3.amazonaws.com/wp-content/uploads/2014/12/Myers-Rum.jpg"));
        dataList.add(new Booze(3, "Shock Top", 30.00, "http://scene7.targetimg1.com/is/image/Target/13437395?wid=480&hei=480"));
        dataList.add(new Booze(4, "Vodka", 150.00, "http://iacom1-a.akamaihd.net/produtos/01/00/item/5432/2/5432285_1GG.jpg"));
        dataList.add(new Booze(5, "Rum + Coke", 100.00, "http://liquor.s3.amazonaws.com/wp-content/uploads/2014/12/Myers-Rum.jpg"));
        dataList.add(new Booze(6, "Shock Top", 30.00, "http://scene7.targetimg1.com/is/image/Target/13437395?wid=480&hei=480"));

        dataList.add(new Booze(11, "Vodka", 150.00, "http://iacom1-a.akamaihd.net/produtos/01/00/item/5432/2/5432285_1GG.jpg"));
        dataList.add(new Booze(21, "Rum + Coke", 100.00, "http://liquor.s3.amazonaws.com/wp-content/uploads/2014/12/Myers-Rum.jpg"));
        dataList.add(new Booze(31, "Shock Top", 30.00, "http://scene7.targetimg1.com/is/image/Target/13437395?wid=480&hei=480"));
        dataList.add(new Booze(41, "Vodka", 150.00, "http://iacom1-a.akamaihd.net/produtos/01/00/item/5432/2/5432285_1GG.jpg"));
        dataList.add(new Booze(51, "Rum + Coke", 100.00, "http://liquor.s3.amazonaws.com/wp-content/uploads/2014/12/Myers-Rum.jpg"));
        dataList.add(new Booze(61, "Shock Top", 30.00, "http://scene7.targetimg1.com/is/image/Target/13437395?wid=480&hei=480"));

        dataList.add(new Booze(12, "Vodka", 150.00, "http://iacom1-a.akamaihd.net/produtos/01/00/item/5432/2/5432285_1GG.jpg"));
        dataList.add(new Booze(22, "Rum + Coke", 100.00, "http://liquor.s3.amazonaws.com/wp-content/uploads/2014/12/Myers-Rum.jpg"));
        dataList.add(new Booze(32, "Shock Top", 30.00, "http://scene7.targetimg1.com/is/image/Target/13437395?wid=480&hei=480"));
        dataList.add(new Booze(42, "Vodka", 150.00, "http://iacom1-a.akamaihd.net/produtos/01/00/item/5432/2/5432285_1GG.jpg"));
        dataList.add(new Booze(52, "Rum + Coke", 100.00, "http://liquor.s3.amazonaws.com/wp-content/uploads/2014/12/Myers-Rum.jpg"));
        dataList.add(new Booze(62, "Shock Top", 30.00, "http://scene7.targetimg1.com/is/image/Target/13437395?wid=480&hei=480"));

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
                startActivity(intent);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
