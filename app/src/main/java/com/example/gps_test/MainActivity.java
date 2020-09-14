package com.example.gps_test;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
    private LocationManager mlocationManager;
    private LocationListener mLocationListener;
    private TextView mLatitudeTextView, mLongtitudeTextView;

    private static final long MINIMUM_DISTANCE_FOR_UPDATES = 1; //в метрах
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // в мс

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLatitudeTextView = (TextView)findViewById(R.id.textViewLatitude);
        mLongtitudeTextView = (TextView)findViewById(R.id.textViewLongitude);

        mlocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new MyLocationListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mlocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //просим включить GPS
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Настройка");
            builder.setMessage("Сейчас GPS отключен.\n" + "Включить?");
            builder.setPositiveButton("Да", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Нет", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //необязательно
                    finish();
                }
            });
            builder.create().show();
        }

        //регистрируемся для обновления
        mlocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MINIMUM_TIME_BETWEEN_UPDATES,MINIMUM_DISTANCE_FOR_UPDATES,mLocationListener);
        //получение текущих координат
        showCurrentLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mlocationManager.removeUpdates(mLocationListener);
    }

    public void OnClick(View view) {
        showCurrentLocation();
    }

    protected void showCurrentLocation() {
        Location location = mlocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            mLatitudeTextView.setText(String.valueOf(location.getLatitude()));
            mLongtitudeTextView.setText(String.valueOf(location.getLongitude()));
        }
    }
    //Прослушиваем изменения
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            String message = "Новое местоположение \n Долгота: " + location.getLongitude() + "\n Широта: " + location.getLatitude();
            Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG).show();
            showCurrentLocation();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Toast.makeText(MainActivity.this,"Статус провайдера изменился",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(MainActivity.this,"Провайдер включен пользователем. GPS включен", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(MainActivity.this,"Провайдер заблокирован пользователем. GPS выключен",Toast.LENGTH_LONG).show();
        }
    }

}