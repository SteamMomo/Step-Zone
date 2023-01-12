package com.release.stepzone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.release.stepzone.database.AppDatabase;
import com.release.stepzone.database.HelperStep;
import com.release.stepzone.database.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainScreen extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView textView;
    private SwitchMaterial switchMaterial;
    private boolean isSensorPresent = false;
    private Sensor sensor;
    private boolean running = false;
    private boolean switchKey = false;
    AppDatabase db;
    CircularProgressBar circularProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        requestPermission();
        initializations();
        stepCounterThings();
        switchThings();
        progressBarThings();
        findViewById(R.id.logout_btn).setOnClickListener(v -> {
            logout();
        });

    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation

                        List<HelperStep> helperSteps = db.helperStepDao().getAll();
                        for(HelperStep helperStep: helperSteps)
                            db.helperStepDao().delete(helperStep);

                        List<User> result = db.userDao().getAll();
                        db.userDao().delete(result.get(0));
                        finish();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void progressBarThings() {

        HelperStep helperStep = db.helperStepDao().findByDate(getDate());
        int steps = 0;
        if (helperStep != null) steps = helperStep.totalSteps;
        textView.setText("Steps : " + steps);
        circularProgressBar.setProgressWithAnimation((float) steps, 1000L);
        circularProgressBar.setProgressMax(5000f);
        circularProgressBar.setProgressBarColor(Color.BLUE);
        circularProgressBar.setBackgroundProgressBarColorStart(R.color.light_black);
        circularProgressBar.setBackgroundProgressBarColorEnd(R.color.dark_black);
        circularProgressBar.setBackgroundProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);
        circularProgressBar.setProgressBarWidth(9f);
        circularProgressBar.setBackgroundProgressBarWidth(10f);
        circularProgressBar.setRoundBorder(true);
        circularProgressBar.setStartAngle(180f);
        circularProgressBar.setProgressDirection(CircularProgressBar.ProgressDirection.TO_RIGHT);


    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (running && event.sensor == sensor) {
            int val = (int) event.values[0];
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Calendar c = Calendar.getInstance();
            String date = sdf.format(c.getTime());
            if (switchKey) {
                HelperStep temp = db.helperStepDao().findByDate(date);
                if (temp == null) {
                    db.helperStepDao().insert(new HelperStep(date, val, 0));
                } else {
                    db.helperStepDao().update(new HelperStep(date, val, temp.totalSteps));
                    textView.setText("steps : " + 0);
                }
                switchKey = false;
            }
            HelperStep result = db.helperStepDao().findByDate(date);
            if (result == null) {
                db.helperStepDao().insert(new HelperStep(date, val, 0));
            } else {
                int inc = val - result.initialSteps;
                db.helperStepDao().update(new HelperStep(date, val, result.totalSteps + inc));
                HelperStep temp = db.helperStepDao().findByDate(date);
                circularProgressBar.setProgress((float) temp.totalSteps);
                textView.setText("Total steps : " + temp.totalSteps);
            }

        }

    }

    private String getDate() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        return sdf.format(c.getTime());
    }

    private void switchThings() {

        switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            HelperStep result = db.helperStepDao().findByDate(getDate());
            int steps = 0;
            if (result != null) steps = result.totalSteps;
            textView.setText("Steps : " + steps);
            if (isChecked) {
                running = true;
                switchKey = true;
            } else {

                running = false;
                switchKey = false;
            }
        });

    }

    private void stepCounterThings() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            isSensorPresent = true;
            switchKey = true;
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        } else {
            isSensorPresent = false;
            switchKey = false;
            Toast.makeText(this, "Step counter sensor not present.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializations() {

        textView = findViewById(R.id.text_view);
        switchMaterial = findViewById(R.id.switch1);
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "Step Zone Database").allowMainThreadQueries().build();
        circularProgressBar = findViewById(R.id.circularProgressBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null)
            sensorManager.registerListener(MainScreen.this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
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
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}