package com.boozefy.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.boozefy.android.R;
import com.boozefy.android.model.Order;
import com.boozefy.android.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Mauricio Giordano on 1/4/16.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Booze, 2016 - All rights reserved.
 */
public class OrdersAdapter extends BoozeAdapter<Order, OrdersAdapter.ViewHolder> {

    private User user;
    private OnItemClickListener onItemClickListener = null;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.text_your_order)
        public TextView lTopText;

        @Bind(R.id.text_total_price)
        public TextView lBottomText;

        @Bind(R.id.text_status)
        public TextView lRightText;

        private Context context;
        public int pos = 0;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            this.context = itemView.getContext();

            itemView.setOnClickListener(this);
        }

        public Context getContext() {
            return context;
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(pos);
            }
        }
    }

    public OrdersAdapter(Activity activity) {
        user = User._.load(activity);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
            LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_orders, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.pos = position;

        Order order = dataList.get(position);

        if (user.getLevel() == User.LEVEL.client) {

            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(order.getCreatedAt());
                holder.lTopText.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
            } catch (ParseException e) {
                holder.lTopText.setText("Your order");
            }

            holder.lBottomText.setText("Bs. " + order.getAmount());

            switch (order.getStatus()) {
                case placed:
                    holder.lRightText.setText("Processing...");
                    break;
                case in_transit:
                    holder.lRightText.setText("Out for delivery");
                    break;
                case fulfilled:
                    holder.lRightText.setText("Delivered");
                    break;
                case rejected:
                    holder.lRightText.setText("Rejected");
                    break;
            }
        } else {
            holder.lTopText.setText(order.getClient().getName());
            holder.lBottomText.setText(order.getAddress());

            switch (order.getStatus()) {
                case placed:
                    holder.lRightText.setText("Waiting for delivery");
                    break;
                case in_transit:
                    holder.lRightText.setText("In transit");
                    break;
                case fulfilled:
                    holder.lRightText.setText("Delivered");
                    break;
                case rejected:
                    holder.lRightText.setText("Rejected");
                    break;
            }
        }
    }

}

