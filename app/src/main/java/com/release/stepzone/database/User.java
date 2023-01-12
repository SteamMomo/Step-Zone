package com.release.stepzone.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "user_name")
    String userName;

    @ColumnInfo(name = "password")
    String password;

    @ColumnInfo(name = "step_goal")
    long stepGoal;

    @ColumnInfo(name = "time_stamp")
    long timeStamp;

    public User(@NonNull String userName, String password, long stepGoal, long timeStamp) {
        this.userName = userName;
        this.password = password;
        this.stepGoal = stepGoal;
        this.timeStamp = timeStamp;
    }
}
