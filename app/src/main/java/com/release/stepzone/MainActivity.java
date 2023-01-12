package com.release.stepzone;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.release.stepzone.database.AppDatabase;
import com.release.stepzone.database.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    AppDatabase db;
    TextInputEditText userName, password;
    TextView signUpText;
    Boolean isLogin;
    Button btn;
    private DatabaseReference database;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission();
        initializations();
        signUpTextThings();
        btnThings();
    }

    private void btnThings() {

        btn.setOnClickListener(v -> {
            String name = Objects.requireNonNull(userName.getText()).toString().trim();
            String pass = Objects.requireNonNull(password.getText()).toString().trim();
            name = name.toLowerCase(Locale.ROOT);
            if (!checkName(name)) return;
            if (!checkPass(pass)) return;
            long timeStamp = System.currentTimeMillis();
            User user = new User(name, pass, 5000, timeStamp);
            if (isLogin) {
                String finalName1 = name;
                database.child("users").child(name).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        HashMap map = (HashMap) task.getResult().getValue();
                        if (map != null) {
                            String temp = (String) map.get("user_password");
                            assert temp != null;
                            if (temp.equals(pass)) {
                                db.userDao().insert(new User(finalName1, pass, (long) map.get("user_step_goal"), (Long) map.get("user_timestamp")));
                                takeToMainScreen();
                            } else {
                                userName.setError("Username does not match with this password.");
                                password.setError("Password does not match with this username");
                            }
                        } else {
                            userName.setError("Username does not match with this password.");
                            password.setError("Password does not match with this username");
                        }
                    }
                });
            } else {
                String finalName = name;
                database.child("users").child(name).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        HashMap map = (HashMap) task.getResult().getValue();
                        if (map != null) {
                            String temp = (String) map.get("user_name");
                            assert temp != null;
                            if (temp.equals(finalName)) {
                                userName.setError("Username already taken.");
                            } else {
                                database.child("users").child(finalName).child("user_name").setValue(finalName);
                                database.child("users").child(finalName).child("user_password").setValue(pass);
                                database.child("users").child(finalName).child("user_step_goal").setValue(5000);
                                database.child("users").child(finalName).child("user_timestamp").setValue(timeStamp);
                                db.userDao().insert(user);
                                takeToMainScreen();
                            }
                        } else {
                            database.child("users").child(finalName).child("user_name").setValue(finalName);
                            database.child("users").child(finalName).child("user_password").setValue(pass);
                            database.child("users").child(finalName).child("user_step_goal").setValue(5000);
                            database.child("users").child(finalName).child("user_timestamp").setValue(timeStamp);
                            db.userDao().insert(user);
                            takeToMainScreen();
                        }
                    }
                });
            }
        });

    }

    private void takeToMainScreen() {
        Intent intent = new Intent(this, MainScreen.class);
        startActivity(intent);
        finish();
    }

    private boolean checkPass(String pass) {
        if (pass.length() < 5) {
            password.setError("At least 5 character required");
            return false;
        }
        return true;
    }

    private Boolean checkName(String name) {
        if (name.length() < 3) {
            userName.setError("At least 3 characters required.");
            return false;
        }
        return true;
    }

    private void signUpTextThings() {
        signUpText.setOnClickListener(v -> {
            isLogin = !isLogin;
            if (isLogin) {
                signUpText.setText("Register");
                btn.setText("Login");
            } else {
                signUpText.setText("Login");
                btn.setText("Register");
            }
        });
    }


    private void initializations() {
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "Step Zone Database").allowMainThreadQueries().build();
        userName = findViewById(R.id.edit_text_1);
        password = findViewById(R.id.edit_text_2);
        signUpText = findViewById(R.id.signUpText);
        isLogin = true;
        btn = findViewById(R.id.btn);
        database = FirebaseDatabase.getInstance().getReference();
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED
                | ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.ACTIVITY_RECOGNITION, Manifest.permission.INTERNET}, 1);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        List<User> user = db.userDao().getAll();
        if (user.size() > 0) {
            Intent intent = new Intent(this, MainScreen.class);
            startActivity(intent);
            finish();
        }
    }

    private String getDate() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        return sdf.format(c.getTime());
    }
}