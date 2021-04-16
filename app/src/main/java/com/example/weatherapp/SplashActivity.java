package com.example.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

public class SplashActivity extends AppCompatActivity {

    static final int PERMISSION_ACCESS_BACKGROUND_LOCATION = 0;
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;
    LocationManager locationManager;
    AlertDialog.Builder builder;
    boolean isConnected;
    int permissionRequestResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        initMembers();
        checkPermissionAndConnectivity(permissionRequestResult, locationManager, isConnected);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initMembers();
        checkPermissionAndConnectivity(permissionRequestResult, locationManager, isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void checkPermissionAndConnectivity(int permissionRequestResult, LocationManager locationManager, boolean isConnected) {
        if (permissionRequestResult == 0) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (isConnected) {
                    startWheatherActivity();
                } else {
                    builder.setMessage("Please connect to Internet").setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            } else {
                builder.setMessage("Please enable GPS for current location access").setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        } else {
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    PERMISSION_ACCESS_BACKGROUND_LOCATION);
        }
        startWheatherActivity();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_BACKGROUND_LOCATION: {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    builder.setMessage("Allow the application access to location permission").setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    } else {
                        initMembers();
                        checkPermissionAndConnectivity(permissionRequestResult, locationManager, isConnected);
                    }
            }
        }
    }

    private void startWheatherActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 1000);
    }

    private void initMembers() {
        builder = new AlertDialog.Builder(SplashActivity.this);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        permissionRequestResult = this.checkCallingOrSelfPermission("android.permission.ACCESS_BACKGROUND_LOCATION");
    }

}