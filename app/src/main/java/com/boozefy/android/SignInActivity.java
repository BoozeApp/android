package com.boozefy.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.boozefy.android.helper.GcmHelper;
import com.boozefy.android.model.User;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.listeners.OnLoginListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    public SimpleFacebook mSimpleFacebook = null;

    @Bind(R.id.layout)
    public View lLayout;
    @Bind(R.id.edit_email)
    public EditText lEditEmail;
    @Bind(R.id.edit_password)
    public EditText lEditPassword;
    @Bind(R.id.button_sign_in)
    public Button lButtonSignIn;
    @Bind(R.id.button_facebook)
    public Button lButtonFacebook;
    @Bind(R.id.text_sign_up)
    public TextView lTextSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        GcmHelper.getInstance().make(this);

        User user = User._.load(this);

        if (user != null) {
            Intent intent = new Intent(SignInActivity.this, AddressActivity.class);
            startActivity(intent);
            finish();
        }

        mSimpleFacebook = SimpleFacebook.getInstance();

        lTextSignUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                    startActivity(intent);
                    return true;
                }

                return false;
            }
        });

        lButtonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean err = false;

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
                    Call<User> call = User.getService().auth(
                        lEditEmail.getText().toString(),
                        lEditPassword.getText().toString(),
                        "android",
                        GcmHelper.getInstance().getRegistrationId(SignInActivity.this)
                    );

                    final ProgressDialog dialog = new ProgressDialog(SignInActivity.this);
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
                                User._.save(user, SignInActivity.this);

                                Intent intent = new Intent(SignInActivity.this, AddressActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                if (response.code() == 404) {
                                    Snackbar.make(lLayout,
                                            R.string.snackbar_email_not_found,
                                            Snackbar.LENGTH_LONG).show();
                                } else if (response.code() == 401) {
                                    Snackbar.make(lLayout,
                                            R.string.snackbar_password_incorrect,
                                            Snackbar.LENGTH_LONG).show();
                                } else {
                                    Snackbar.make(lLayout,
                                            R.string.snackbar_check_your_internet_connection,
                                            Snackbar.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            dialog.dismiss();

                            Snackbar.make(lLayout,
                                    R.string.snackbar_check_your_internet_connection,
                                    Snackbar.LENGTH_LONG).show();
                        }
                    });
                }

            }
        });

        lButtonFacebook.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Permission[] permissions = new Permission[]{
                        Permission.EMAIL
                };

                SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                        .setAppId(getString(R.string.fb_id))
                        .setNamespace(getPackageName())
                        .setPermissions(permissions)
                        .build();

                SimpleFacebook.setConfiguration(configuration);

                mSimpleFacebook = SimpleFacebook.getInstance(SignInActivity.this);
                mSimpleFacebook.login(new OnLoginListener() {
                    private boolean retry = true;
                    private OnLoginListener onLoginListener = this;

                    @Override
                    public void onLogin() {
                        String accessToken = mSimpleFacebook.getSession().getAccessToken();

                        Call<User> call = User.getService().auth(
                            accessToken,
                            "android",
                            GcmHelper.getInstance().getRegistrationId(SignInActivity.this)
                        );

                        call.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Response<User> response) {
                                if (response.body() != null) {
                                    User user = response.body();
                                    User._.save(user, SignInActivity.this);

                                    Intent intent = new Intent(SignInActivity.this, AddressActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Snackbar.make(lLayout,
                                        R.string.snackbar_check_your_internet_connection,
                                            Snackbar.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Snackbar.make(lLayout,
                                        R.string.snackbar_check_your_internet_connection,
                                        Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onNotAcceptingPermissions(Permission.Type type) {
                        Snackbar.make(lLayout,
                                R.string.snackbar_you_need_to_accept,
                                Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onThinking() {
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        Snackbar.make(lLayout,
                                R.string.snackbar_check_your_internet_connection,
                                Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFail(String reason) {
                        Snackbar.make(lLayout,
                                R.string.snackbar_check_your_internet_connection,
                                Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
    }
}
