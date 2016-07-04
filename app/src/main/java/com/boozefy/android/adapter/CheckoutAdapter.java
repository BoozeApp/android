package com.boozefy.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.boozefy.android.R;
import com.boozefy.android.model.Beverage;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Mauricio Giordano on 1/12/16.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Booze, 2016 - All rights reserved.
 */
public class CheckoutAdapter extends BoozeAdapter<CheckoutAdapter.BoozeAmount, CheckoutAdapter.ViewHolder> {

    public static class BoozeAmount {
        public Beverage beverage;
        public Integer amount;

        public BoozeAmount(Beverage beverage, Integer amount) {
            this.beverage = beverage;
            this.amount = amount;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private Context context;

        @Bind(R.id.image_booze_picture)
        public ImageView lPicture;
        @Bind(R.id.text_booze_name)
        public TextView lName;
        @Bind(R.id.text_booze_price_unit)
        public TextView lPriceUnit;
        @Bind(R.id.text_booze_price_total)
        public TextView lPriceTotal;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            this.context = itemView.getContext();
        }

        public Context getContext() {
            return context;
        }

        public String getString(int id) {
            return context.getString(id);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
            LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_checkout, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BoozeAmount boozeAmount = dataList.get(position);

        Picasso.with(holder.getContext())
                .load(boozeAmount.beverage.getPicture())
                //.placeholder(R.drawable.placeholder)
                //.error(R.drawable.error)
                .resize(64, 64)
                .centerInside()
                //.tag(context)
                .into(holder.lPicture);
        holder.lName.setText(boozeAmount.amount + "x " + boozeAmount.beverage.getName());

        holder.lPriceUnit.setText(String.format(Locale.ENGLISH, holder.getString(R.string.text_price), boozeAmount.beverage.getPrice()) + " / u");
        holder.lPriceTotal.setText(String.format(Locale.ENGLISH, holder.getString(R.string.text_price), (boozeAmount.beverage.getPrice() * boozeAmount.amount)));
    }
}
