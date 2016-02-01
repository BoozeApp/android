package com.boozefy.android.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ScrollView;

import com.boozefy.android.R;

/**
 * Created by Mauricio Giordano on 1/22/16.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Booze, 2016 - All rights reserved.
 */

public class SwipeRefreshLayout extends android.support.v4.widget.SwipeRefreshLayout {
    private AbsListView listView = null;
    private ScrollView scrollView = null;
    private RecyclerView recyclerView = null;

    private CanChildScrollUpCallback mCanChildScrollUpCallback;

    public interface CanChildScrollUpCallback {
        boolean canSwipeRefreshChildScrollUp();
    }

    public void setCanChildScrollUpCallback(CanChildScrollUpCallback canChildScrollUpCallback) {
        mCanChildScrollUpCallback = canChildScrollUpCallback;
    }

    public SwipeRefreshLayout(Context context) {
        super(context);
        setColors();
    }

    public SwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setColors();
    }

    public void setScrollView(AbsListView listView) {
        this.listView = listView;
    }

    public void setScrollView(ScrollView scrollView) {
        this.scrollView = scrollView;
    }

    public void setScrollView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public boolean canChildScrollUp() {
        if (mCanChildScrollUpCallback != null) return mCanChildScrollUpCallback.canSwipeRefreshChildScrollUp();
        if(listView == null && scrollView == null && recyclerView == null) return super.canChildScrollUp();

        int pos = 0;

        try {
            if(listView != null) {
                pos = listView.getChildAt(0).getTop();
            } else if (recyclerView != null) {
                pos = recyclerView.getChildAt(0).getTop();
            } else {
                pos = - scrollView.getScrollY();
            }
        } catch(Exception e) {}

        return pos < 0;
    }

    private void setColors() {
        setSoundEffectsEnabled(true);
        setColorSchemeResources(
            R.color.swipe_1,
            R.color.swipe_2,
            R.color.swipe_3
        );
    }
}
