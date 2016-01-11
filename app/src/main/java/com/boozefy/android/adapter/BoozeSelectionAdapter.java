package com.boozefy.android.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.boozefy.android.R;
import com.boozefy.android.model.Booze;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Mauricio Giordano on 1/4/16.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Booze, 2016 - All rights reserved.
 */
public class BoozeSelectionAdapter extends BoozeAdapter<Booze, BoozeSelectionAdapter.ViewHolder> {

    private OnTotalPriceChangedListener onTotalPriceChangedListener = null;
    private HashMap<Booze, Integer> selectedBoozes = null;

    public interface OnTotalPriceChangedListener {
        void onTotalPriceChanged(double newPrice);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.image_booze_picture)
        public ImageView lPicture;
        @Bind(R.id.text_booze_name)
        public TextView lName;
        @Bind(R.id.text_booze_price)
        public TextView lPrice;
        @Bind(R.id.wrap_picker_amount)
        public TextView lPickerAmount;
        @Bind(R.id.wrap_picker_plus)
        public Button lPickerPlus;
        @Bind(R.id.wrap_picker_minus)
        public Button lPickerMinus;

        private Context context;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            this.context = itemView.getContext();
        }

        public void refreshListeners(final Booze booze) {
            int amount = selectedBoozes.get(booze) == null ? 0 : selectedBoozes.get(booze);

            refreshContent(amount, booze.getMax());

            lPickerPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int old = selectedBoozes.get(booze) == null ? 0 : selectedBoozes.get(booze);

                    if (old == booze.getMax()) return;

                    selectedBoozes.put(booze, old + 1);
                    lPickerAmount.setText(String.valueOf(old + 1));

                    if (old == 0 || old + 1 == booze.getMax()) {
                        refreshContent(old + 1, booze.getMax());
                    }

                    calculateTotalPrice();
                }
            });

            lPickerMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int old = selectedBoozes.get(booze) == null ? 0 : selectedBoozes.get(booze);

                    if (old - 1 == 0 || old == 0) {
                        selectedBoozes.remove(booze);
                        lPickerAmount.setText(String.valueOf(0));
                        refreshContent(0, booze.getMax());
                    } else {
                        if (old == booze.getMax()) {
                            refreshContent(old - 1, booze.getMax());
                        }

                        selectedBoozes.put(booze, old - 1);
                        lPickerAmount.setText(String.valueOf(old - 1));
                    }

                    calculateTotalPrice();
                }
            });
        }

        private void refreshContent(int amount, int max) {
            if (amount > 0) {
                if (amount == max) {
                    lPickerPlus.setTextColor(ContextCompat.getColor(context, R.color.inactive));
                    lPickerPlus.setBackgroundResource(R.drawable.circular_button_inactive);
                } else {
                    lPickerPlus.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                    lPickerPlus.setBackgroundResource(R.drawable.circular_button);
                }

                lPickerAmount.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                lPickerMinus.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                lPickerMinus.setBackgroundResource(R.drawable.circular_button);
            } else {
                lPickerAmount.setTextColor(ContextCompat.getColor(context, R.color.inactive));
                lPickerMinus.setTextColor(ContextCompat.getColor(context, R.color.inactive));
                lPickerMinus.setBackgroundResource(R.drawable.circular_button_inactive);
            }

            lPickerAmount.setText(String.valueOf(amount));
        }

        public Context getContext() {
            return context;
        }
    }

    public BoozeSelectionAdapter() {
        selectedBoozes = new HashMap<>();
    }

    public void setOnTotalPriceChangedListener(OnTotalPriceChangedListener onTotalPriceChangedListener) {
        this.onTotalPriceChangedListener = onTotalPriceChangedListener;
    }

    public HashMap<Long, Integer> getSelectedBoozes() {
        HashMap<Long, Integer> result = new HashMap<>();

        for (Map.Entry<Booze, Integer> entry : selectedBoozes.entrySet()) {
            result.put(entry.getKey().getId(), entry.getValue());
        }

        return result;
    }

    private void calculateTotalPrice() {
        if (onTotalPriceChangedListener == null) return;

        double total = 0.0;

        for (Map.Entry<Booze, Integer> entry : selectedBoozes.entrySet()) {
            total += entry.getValue() * entry.getKey().getPrice();
        }

        onTotalPriceChangedListener.onTotalPriceChanged(total);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
            LayoutInflater.from(parent.getContext())
            .inflate(R.layout.adapter_booze_selection, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Booze booze = dataList.get(position);
        holder.refreshListeners(booze);

        Picasso.with(holder.getContext())
            .load(booze.getPicture())
            //.placeholder(R.drawable.placeholder)
            //.error(R.drawable.error)
            .resize(64, 64)
            .centerInside()
            //.tag(context)
            .into(holder.lPicture);
        holder.lName.setText(booze.getName());
        holder.lPrice.setText("Bs. " + booze.getPrice());
    }

}