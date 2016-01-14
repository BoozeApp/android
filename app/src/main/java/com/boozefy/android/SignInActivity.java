package com.boozefy.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

    @Bind(R.id.toolbar)
    public Toolbar lToolbar;
    @Bind(R.id.button_facebook)
    public Button lButtonFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        setSupportActionBar(lToolbar);

        User user = User._.load(this);

        if (user != null) {
            Intent intent = new Intent(SignInActivity.this, AddressActivity.class);
            startActivity(intent);
            finish();
        }

        mSimpleFacebook = SimpleFacebook.getInstance();

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

                        Call<User> call = User.getService().auth(accessToken);
                        call.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Response<User> response) {
                                if (response.body() != null) {
                                    User user = response.body();
                                    User._.save(user, SignInActivity.this);

                                    Intent intent = new Intent(SignInActivity.this, AddressActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Log.d("USER", "Err " + t.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onNotAcceptingPermissions(Permission.Type type) {

                    }

                    @Override
                    public void onThinking() {
                    }

                    @Override
                    public void onException(Throwable throwable) {

                    }

                    @Override
                    public void onFail(String reason) {

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
