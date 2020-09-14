package com.example.gps_test;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location mLocation;
    private TextView mLatitudeTextView, mLongitudeTextView;

    private static final long MINIMUM_DISTANCE_FOR_UPDATES = 10; // в метрах
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 2000; // в мс

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mLatitudeTextView = (TextView) findViewById(R.id.textViewLatitude);
        mLongitudeTextView = (TextView) findViewById(R.id.textViewLongitude);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        String provider = mLocationManager.getBestProvider(criteria, true);
        mLocation = mLocationManager.getLastKnownLocation(provider);
        mLocationListener = new MyLocationListener();

        showCurrentLocation(mLocation);

        // Регистрируемся для обновлений
        mLocationManager.requestLocationUpdates(provider,
                MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_FOR_UPDATES,
                mLocationListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(mLocationListener);
    }

    public void onClick(View v) {
        showCurrentLocation(mLocation);
    }

    protected void showCurrentLocation(Location location) {
        if (location != null) {
            mLatitudeTextView.setText(String.valueOf(location.getLatitude()));
            mLongitudeTextView.setText(String.valueOf(location.getLongitude()));
        }
    }

    // Прослушиваем изменения
    private class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            String message = "Новое местоположение Долгота: " +
            location.getLongitude() + "Широта: " + location.getLatitude();
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG)
                    .show();
            showCurrentLocation(mLocation);
        }

        public void onStatusChanged(String s, int i, Bundle b) {
            Toast.makeText(MainActivity.this, "Статус провайдера изменился",
                    Toast.LENGTH_LONG).show();
        }

        public void onProviderDisabled(String s) {
            Toast.makeText(MainActivity.this,
                    "Провайдер заблокирован пользователем. GPS выключен",
                    Toast.LENGTH_LONG).show();
        }

        public void onProviderEnabled(String s) {
            Toast.makeText(MainActivity.this,
                    "Провайдер включен пользователем. GPS включён",
                    Toast.LENGTH_LONG).show();
        }
    }
}