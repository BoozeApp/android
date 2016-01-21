package com.boozefy.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.boozefy.android.OrderActivity;
import com.boozefy.android.R;
import com.boozefy.android.adapter.BoozeAdapter;
import com.boozefy.android.adapter.OrdersAdapter;
import com.boozefy.android.model.Order;
import com.boozefy.android.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    public static final int FOR_DELIVERY = 1;
    public static final int IN_TRANSIT   = 2;
    public static final int DELIVERED    = 3;
    public static final int MY_ORDERS    = 4;
    public static final int PAST_ORDERS  = 5;

    private User user;
    private int mType = FOR_DELIVERY;

    private OrdersAdapter adapter;
    private List<Order> orderList = new ArrayList<>();

    public OrdersFragment() {
    }

    public static OrdersFragment newInstance(int type) {
        OrdersFragment fragment = new OrdersFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mType = getArguments().getInt("type");
        }

        user = User._.load(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_orders_list, container, false);

        adapter = new OrdersAdapter(getActivity());

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);
        }

        onRefresh();

        adapter.setOnItemClickListener(new BoozeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                Order order = adapter.getItem(pos);
                Order._.save(order, getActivity());

                Intent intent = new Intent(getActivity(), OrderActivity.class);
                intent.putExtra("orderId", order.getId());
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onRefresh() {
        final Call<List<Order>> ordersCall;

        switch (mType) {
            case FOR_DELIVERY:
                ordersCall = Order.getService().placed(user.getAccessToken());
                break;
            case IN_TRANSIT:
                ordersCall = Order.getService().inTransit(user.getAccessToken());
                break;
            case DELIVERED:
                ordersCall = Order.getService().fulfilled(user.getAccessToken());
                break;
            case MY_ORDERS:
                ordersCall = Order.getService().find(user.getAccessToken());
                break;
            case PAST_ORDERS:
                ordersCall = Order.getService().find(user.getAccessToken());
                break;
            default:
                ordersCall = Order.getService().find(user.getAccessToken());
                break;
        }

        ordersCall.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Response<List<Order>> response) {
                if (response.body() != null) {
                    orderList = response.body();
                    List<Order> newOrderList = new ArrayList<Order>();

                    if (mType == MY_ORDERS) {
                        for (Order order : orderList) {
                            if (order.getStatus() == Order.STATUS.placed ||
                                order.getStatus() == Order.STATUS.in_transit) {
                                newOrderList.add(order);
                            }
                        }

                        orderList = newOrderList;
                    } else if (mType == PAST_ORDERS) {
                        for (Order order : orderList) {
                            if (order.getStatus() == Order.STATUS.fulfilled ||
                                    order.getStatus() == Order.STATUS.rejected) {
                                newOrderList.add(order);
                            }
                        }

                        orderList = newOrderList;
                    }

                    adapter.setDataList(orderList);
                } else {

                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
}
