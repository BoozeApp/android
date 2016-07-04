package com.boozefy.android;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.boozefy.android.helper.GeocoderHelper;
import com.boozefy.android.model.User;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    public Toolbar lToolbar;
    @Bind(R.id.layout_client)
    public View lLayoutClient;
    @Bind(R.id.action_current_location)
    public Button lCurrentLocation;
    @Bind(R.id.button_orders)
    public Button lButtonOrders;
    @Bind(R.id.layout_staff)
    public View lLayoutStaff;
    @Bind(R.id.button_review_orders)
    public Button lButtonReviewOrders;

    public static final int REQUEST_PERMISSION_FINE_LOCATION = 10;
    private boolean foundLocation = false;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ButterKnife.bind(this);

        setSupportActionBar(lToolbar);

        user = User._.load(this);

        if (user.getLevel() == User.LEVEL.client) {
            lLayoutClient.setVisibility(View.VISIBLE);
            lLayoutStaff.setVisibility(View.GONE);

            lCurrentLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (ActivityCompat.checkSelfPermission(AddressActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(AddressActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_PERMISSION_FINE_LOCATION);
                    } else {
                        gatherUserLocation();
                    }
                }
            });

            lButtonOrders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AddressActivity.this, OrdersActivity.class);
                    startActivity(intent);
                }
            });

        } else {
            lLayoutClient.setVisibility(View.GONE);
            lLayoutStaff.setVisibility(View.VISIBLE);

            lButtonReviewOrders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AddressActivity.this, OrdersActivity.class);
                    startActivity(intent);
                }
            });

        }

    }

    private void gatherUserLocation() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.dialog_loading_location));
        progressDialog.show();

        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            private boolean lock = false;

            @Override
            public void onLocationChanged(Location location) {
                if (lock || foundLocation) return;
                lock = true;
                foundLocation = true;

                Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                List<Address> addresses;

                try {
                    addresses = gcd.getFromLocation(location.getLatitude(),
                            location.getLongitude(), 1);

                    String address = addresses.get(0).getAddressLine(0);

                    GeocoderHelper.gatherFromAddress(address, new GeocoderHelper.OnLocationGathered() {
                        @Override
                        public void onGathered(GeocoderHelper.Location location) {
                            progressDialog.dismiss();
                            displayDialog(location);
                        }
                    });

                } catch (IOException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.d("LOCATION", "STATUSCHANGED " + s);
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.d("LOCATION", "PROVIDERENABLED " + s);
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d("LOCATION", "PROVIDERDISABLED " + s);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!foundLocation) {
                    progressDialog.dismiss();
                    foundLocation = true;

                    Snackbar.make(lToolbar, R.string.snackbar_could_not_detect_gps,
                                                                Snackbar.LENGTH_LONG).show();
                    displayDialog(null);
                }
            }
        }, 10000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                foundLocation = false;
            }
        }, 12000);
    }

    private void displayDialog(GeocoderHelper.Location location) {
        View layout = getLayoutInflater().inflate(R.layout.dialog_location, null, false);

        final EditText lEditStreet = (EditText) layout.findViewById(R.id.edit_street);
        final EditText lEditNumber = (EditText) layout.findViewById(R.id.edit_number);
        final EditText lEditDescri = (EditText) layout.findViewById(R.id.edit_description);
        final Spinner lSpinnerCity = (Spinner) layout.findViewById(R.id.spinner_city);

        if (location != null) {
            lEditStreet.setText(location.street);
            lEditNumber.setText(location.number);
        }

        lSpinnerCity.setEnabled(false);

        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle(R.string.dialog_title_your_address)
            .setView(layout)
            .setPositiveButton(R.string.button_deliver_here, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final ProgressDialog progressDialog = new ProgressDialog(AddressActivity.this);
                    progressDialog.setMessage(getString(R.string.dialog_loading_location));
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    String addressLine = lEditStreet.getText().toString() + ", " +
                                         lEditNumber.getText().toString() + " - " +
                                         lSpinnerCity.getSelectedItem().toString() + ", Bolivia";

                    GeocoderHelper.gatherFromAddress(addressLine, new GeocoderHelper.OnLocationGathered() {
                        @Override
                        public void onGathered(GeocoderHelper.Location location) {
                            progressDialog.dismiss();

                            if (location == null) {
                                Snackbar.make(lToolbar, R.string.snackbar_could_not_detect_gps,
                                        Snackbar.LENGTH_LONG).show();
                                return;
                            }

                            if (location.street == null) {
                                location.street = lEditStreet.getText().toString();
                            }

                            if (location.number == null) {
                                location.number = lEditNumber.getText().toString();
                            }

                            location.description = lEditDescri.getText().toString();
                            location.zipCode = "";

                            verifyTelephone(location);
                        }
                    });
                }
            })
            .setNegativeButton(R.string.button_cancel, null)
            .create();

        try {
            dialog.show();
        } catch (Exception e) {

        }
    }

    private void verifyTelephone(final GeocoderHelper.Location location) {

        View layout = getLayoutInflater().inflate(R.layout.dialog_telephone, null, false);

        final EditText lEditTelephone = (EditText) layout.findViewById(R.id.edit_telephone);

        if (user.getTelephone() != null) {
            lEditTelephone.setText(user.getTelephone());
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_your_contact)
                .setView(layout)
                .setPositiveButton(R.string.button_done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        final ProgressDialog progressDialog = new ProgressDialog(AddressActivity.this);
                        progressDialog.setMessage(getString(R.string.dialog_loading_generic));
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        User.getService().me(
                            user.getAccessToken(),
                            lEditTelephone.getText().toString()
                        ).enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Response<User> response) {
                                if (response.body() != null) {
                                    user.setTelephone(lEditTelephone.getText().toString());
                                    User._.save(user, AddressActivity.this);

                                    Intent intent = new Intent(AddressActivity.this, BoozeSelectionActivity.class);
                                    intent.putExtra("location", location.toString());
                                    startActivity(intent);
                                } else {
                                    Snackbar.make(lToolbar,
                                        R.string.snackbar_check_your_internet_connection,
                                            Snackbar.LENGTH_LONG).show();
                                }

                                progressDialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Snackbar.make(lToolbar,
                                    R.string.snackbar_check_your_internet_connection,
                                        Snackbar.LENGTH_LONG).show();

                                progressDialog.dismiss();
                            }
                        });

                    }
                })
                .setNegativeButton(R.string.button_cancel, null)
                .create();

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_address, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            User._.remove(this);
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                gatherUserLocation();
            } else {
                Snackbar.make(lToolbar, R.string.snackbar_you_need_to_allow, Snackbar.LENGTH_LONG).show();
                displayDialog(null);
            }
        }
    }
}
