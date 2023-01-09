package com.release.stepzone;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 101;
    private SensorManager sensorManager;
    private TextView textView;
    private boolean isSensorPresent = false;
    private Sensor sensor;
    private int steps = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission();
        initializations();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        stepCounterThings();

    }

    private void stepCounterThings() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            isSensorPresent = true;
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        } else {
            isSensorPresent = false;
            Toast.makeText(this, "Step counter sensor not present.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializations() {
        textView = findViewById(R.id.text_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null)
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null)
            sensorManager.unregisterListener(this, sensor);
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor == sensor) {
            steps = (int) event.values[0];
            textView.setText("Steps : " + steps);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}