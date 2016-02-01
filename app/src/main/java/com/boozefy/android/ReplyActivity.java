package com.boozefy.android;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.boozefy.android.model.Order;
import com.boozefy.android.model.User;

import retrofit2.Callback;
import retrofit2.Response;

public class ReplyActivity extends AppCompatActivity {

    private User user;
    private long orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            orderId = getIntent().getLongExtra("orderId", -1);
        } else {
            orderId = savedInstanceState.getLong("orderId", -1);
        }

        if (orderId == -1) {
            finish();
            return;
        }

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel((int) orderId);

        user = User._.load(this);

        if (user == null) {
            finish();
            return;
        }

        View layout = getLayoutInflater().inflate(R.layout.dialog_reply_message, null, false);

        final EditText message = (EditText) layout.findViewById(R.id.edit_message);

        AlertDialog dialog = new AlertDialog.Builder(ReplyActivity.this)
            .setTitle(R.string.dialog_title_send_message)
            .setView(layout)
            .setPositiveButton(R.string.button_reply, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final ProgressDialog dialog = new ProgressDialog(ReplyActivity.this);
                    dialog.setMessage(getString(R.string.dialog_sending_message));
                    dialog.setIndeterminate(true);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                    Order.getService().message(
                            orderId,
                            user.getAccessToken(),
                            message.getText().toString()
                    ).enqueue(new Callback<Order>() {
                        @Override
                        public void onResponse(Response<Order> response) {
                            dialog.dismiss();

                            if (response.body() != null) {
                                Toast.makeText(ReplyActivity.this,
                                        R.string.snackbar_message_sent,
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ReplyActivity.this,
                                        R.string.snackbar_check_your_internet_connection,
                                        Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            dialog.dismiss();

                            Toast.makeText(ReplyActivity.this,
                                    R.string.snackbar_check_your_internet_connection,
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }
            })
            .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            })
            .create();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                finish();
            }
        });

        dialog.show();
    }

    @Override
    public void finish() {
        Intent intent = new Intent(this, AddressActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        super.finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("orderId", orderId);
    }
}
