package com.boozefy.android.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mauricio Giordano on 1/4/16.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Booze, 2016 - All rights reserved.
 */
public abstract class BoozeAdapter<D, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected List<D> dataList = new ArrayList<>();

    public void setDataList(@NonNull List<D> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public D getItem(int pos) {
        if (dataList.size() <= pos) return null;
        return dataList.get(pos);
    }

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }
}
