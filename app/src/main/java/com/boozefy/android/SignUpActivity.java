package com.boozefy.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.boozefy.android.helper.GcmHelper;
import com.boozefy.android.model.User;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    public Toolbar lToolbar;
    @Bind(R.id.layout)
    public View lLayout;
    @Bind(R.id.edit_name)
    public EditText lEditName;
    @Bind(R.id.edit_email)
    public EditText lEditEmail;
    @Bind(R.id.edit_password)
    public EditText lEditPassword;
    @Bind(R.id.button_sign_up)
    public Button lButtonSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        GcmHelper.getInstance().make(this);

        ButterKnife.bind(this);

        setSupportActionBar(lToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lButtonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean err = false;

                if (lEditName.getText().toString().length() < 4) {
                    lEditName.setError(getString(R.string.error_text_too_short));
                    err = true;
                }

                if (lEditEmail.getText().toString().length() < 4) {
                    lEditEmail.setError(getString(R.string.error_text_too_short));
                    err = true;
                } else if (!User.isValidEmailAddress(lEditEmail.getText().toString())) {
                    lEditEmail.setError(getString(R.string.error_invalid_email));
                    err = true;
                }

                if (lEditPassword.getText().toString().length() < 4) {
                    lEditPassword.setError(getString(R.string.error_text_too_short));
                    err = true;
                }

                if (!err) {
                    Call<User> call = User.getService().create(
                            lEditName.getText().toString(),
                            lEditEmail.getText().toString(),
                            lEditPassword.getText().toString(),
                            "android",
                            GcmHelper.getInstance().getRegistrationId(SignUpActivity.this)
                    );

                    final ProgressDialog dialog = new ProgressDialog(SignUpActivity.this);
                    dialog.setMessage(getString(R.string.dialog_loading_generic));
                    dialog.setIndeterminate(true);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Response<User> response) {
                            dialog.dismiss();
                            if (response.body() != null) {
                                User user = response.body();
                                User._.save(user, SignUpActivity.this);

                                Intent intent = new Intent(SignUpActivity.this, AddressActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                if (response.code() == 406) {
                                    Snackbar.make(lLayout,
                                            R.string.snackbar_email_already_in_use,
                                            Snackbar.LENGTH_LONG).show();
                                } else {
                                    Log.d("FAIL", response.message());
                                    Snackbar.make(lLayout,
                                            R.string.snackbar_check_your_internet_connection,
                                            Snackbar.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            dialog.dismiss();
                            Log.d("FAIL", t.getMessage());
                            Snackbar.make(lLayout,
                                    R.string.snackbar_check_your_internet_connection,
                                    Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
