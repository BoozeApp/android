package com.boozefy.android;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by mauricio on 5/27/16.
 */
public class GPSTrackerActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (mLastLocation != null) {
                Log.d("GPSTracker", "mLastLocation != null");
                Intent intent = new Intent();
                intent.putExtra("Longitude", mLastLocation.getLongitude());
                intent.putExtra("Latitude", mLastLocation.getLatitude());
                setResult(1, intent);
                finish();
            } else {
                Log.d("GPSTracker", "mLastLocation == null");
            }

        } catch (SecurityException e) {
            e.printStackTrace();
            finish();
        }
    }

    @Override
    public void onConnectionSuspended(int i) { }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) { }
}
