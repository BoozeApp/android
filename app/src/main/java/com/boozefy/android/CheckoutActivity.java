package com.boozefy.android;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.boozefy.android.model.Booze;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

public class CheckoutActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    public Toolbar lToolbar;

    private HashMap<Booze, Integer> selectedBoozesModel;
    private HashMap<Long, Integer> selectedBoozes;

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

        for (Map.Entry<Long, Integer> entry : selectedBoozes.entrySet()) {
            Booze booze = realm.where(Booze.class).equalTo("id", entry.getKey()).findFirst();

            selectedBoozesModel.put(booze, entry.getValue());
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
