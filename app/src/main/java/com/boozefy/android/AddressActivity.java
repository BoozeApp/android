package com.boozefy.android;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddressActivity extends AppCompatActivity {

    @Bind(R.id.action_current_location)
    public Button lCurrentLocation;

    @Bind(R.id.toolbar)
    public Toolbar lToolbar;

    @Bind(R.id.fab)
    public FloatingActionButton lFab;

    public static final int REQUEST_PERMISSION_FINE_LOCATION = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ButterKnife.bind(this);

        setSupportActionBar(lToolbar);

        lFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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

                /* Intent intent = new Intent(AddressActivity.this, BoozeSelectionActivity.class);
                startActivity(intent); */
            }
        });
    }

    private void gatherUserLocation() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading location...");
        progressDialog.show();

        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            private boolean lock = false;
            @Override
            public void onLocationChanged(Location location) {
                if (lock) return;
                progressDialog.dismiss();
                lock = true;

                Log.d("LOCATION", "LATITUDE  " + location.getLatitude());
                Log.d("LOCATION", "LONGITUDE " + location.getLongitude());

                String cityName = null;
                Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(location.getLatitude(),
                            location.getLongitude(), 1);

                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();

                    Log.d("LOCATION", "ADDRESS    " + address);
                    Log.d("LOCATION", "CITY       " + city);
                    Log.d("LOCATION", "STATE      " + state);
                    Log.d("LOCATION", "COUNTRY    " + country);
                    Log.d("LOCATION", "POSTALCODE " + postalCode);
                    Log.d("LOCATION", "KNOWNNAME  " + knownName);

                } catch (IOException e) {
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


        /*long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }*/


        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10,
            new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    Log.d("LOCATION", "LATITUDE  " + location.getLatitude());
                    Log.d("LOCATION", "LONGITUDE " + location.getLongitude());

                     /*------- To get city name from coordinates -------- */
                    String cityName = null;
                    Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                    List<Address> addresses;
                    try {
                        addresses = gcd.getFromLocation(location.getLatitude(),
                                location.getLongitude(), 1);
                        if (addresses.size() > 0) {
                            System.out.println(addresses.get(0).getLocality());
                            cityName = addresses.get(0).getLocality();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Log.d("LOCATION", "CITY " + cityName);
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            }
        );
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
        if (id == R.id.action_settings) {
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
                Snackbar.make(lFab, "You need to accept", Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
