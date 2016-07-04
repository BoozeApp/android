package com.boozefy.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.boozefy.android.R;
import com.boozefy.android.model.Message;
import com.boozefy.android.model.User;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mauricio on 5/30/16.
 */
public class MessageAdapter extends BoozeAdapter<Message, MessageAdapter.ViewHolder> {

    private List<Message> dataList;
    private User user;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Context context;

        @Bind(R.id.friendLayout)
        public View friendLayout;
        @Bind(R.id.meLayout)
        public View meLayout;
        @Bind(R.id.friendText)
        public TextView friendText;
        @Bind(R.id.meText)
        public TextView meText;
        @Bind(R.id.friendDate)
        public TextView friendDate;
        @Bind(R.id.meDate)
        public TextView meDate;
        @Bind(R.id.progressBar)
        public ProgressBar progressBar;

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

    public MessageAdapter(User user) {
        this.dataList = new ArrayList<>();
        this.user = user;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_message, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Message chat = dataList.get(position);

        viewHolder.progressBar.setVisibility(View.GONE);

        String date;

        try {
            Calendar datePost = new GregorianCalendar();
            datePost.setTime(new Timestamp(Long.parseLong(chat.getCreatedAt()) * 1000));
            datePost.setTimeZone(TimeZone.getTimeZone("UTC"));

            Calendar dateNow = new GregorianCalendar();
            dateNow.setTimeZone(TimeZone.getTimeZone("UTC"));

            long duration = dateNow.getTimeInMillis() - datePost.getTimeInMillis();

            long diffMinutes = (duration / 1000) / 60;
            long diffHours = (duration / 1000) / 3600;
            long diffDays = (duration / 1000) / 86400;

            diffMinutes -= 60*diffHours;
            diffHours -= 24*diffDays;

            if (diffMinutes + 1 == 60) {
                diffMinutes = 0;
                diffHours++;
            }

            if (diffHours + 1 == 24) {
                diffHours = 0;
                diffDays++;
            }

            if (diffDays > 0) {
                date = String.format("%02dd", diffDays);
            } else if (diffHours > 0) {
                date = String.format("%02dh", diffHours);
            } else if (diffMinutes > 0) {
                date = String.format("%02dm", diffMinutes);
            } else if (duration > 0) {
                date = String.format("%02ds", duration/1000);
            } else {
                date = "";
            }

        } catch(Exception e) {
            date = "";
        }

        if (chat.getSender().getId() == user.getId()) {
            viewHolder.friendLayout.setVisibility(View.GONE);
            viewHolder.meLayout.setVisibility(View.VISIBLE);

            viewHolder.meText.setText(chat.getMessage());
            viewHolder.meDate.setText(date);

            if (date.equals("")) {
                viewHolder.meDate.setVisibility(View.GONE);
            } else {
                viewHolder.meDate.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.friendLayout.setVisibility(View.VISIBLE);
            viewHolder.meLayout.setVisibility(View.GONE);

            viewHolder.friendText.setText(chat.getMessage());
            viewHolder.friendDate.setText(date);

            if (date.equals("")) {
                viewHolder.friendDate.setVisibility(View.GONE);
            } else {
                viewHolder.friendDate.setVisibility(View.VISIBLE);
            }
        }
    }
}
