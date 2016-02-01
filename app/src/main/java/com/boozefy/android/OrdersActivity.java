package com.boozefy.android;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.boozefy.android.fragment.OrdersFragment;
import com.boozefy.android.model.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OrdersActivity extends AppCompatActivity {

    private User user;

    @Bind(R.id.toolbar)
    public Toolbar lToolbar;

    @Bind(R.id.viewpager)
    public ViewPager lViewPager;

    @Bind(R.id.tabs)
    public TabLayout lTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        ButterKnife.bind(this);
        setSupportActionBar(lToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = User._.load(this);

        if (user.getLevel() == User.LEVEL.client) {
            initClient();
        } else {
            initStaff();
        }

        lTabLayout.setupWithViewPager(lViewPager);
    }

    private void initClient() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(OrdersFragment.newInstance(OrdersFragment.MY_ORDERS), getString(R.string.tab_my_orders));
        adapter.addFragment(OrdersFragment.newInstance(OrdersFragment.PAST_ORDERS), getString(R.string.tab_past_orders));
        lViewPager.setAdapter(adapter);
    }

    private void initStaff() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(OrdersFragment.newInstance(OrdersFragment.FOR_DELIVERY), getString(R.string.tab_for_delivery));
        adapter.addFragment(OrdersFragment.newInstance(OrdersFragment.IN_TRANSIT), getString(R.string.tab_in_transit));
        adapter.addFragment(OrdersFragment.newInstance(OrdersFragment.DELIVERED), getString(R.string.tab_delivered));
        lViewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
