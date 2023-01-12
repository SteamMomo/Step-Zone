package com.release.stepzone.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class HelperStep {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "initial_steps")
    public int initialSteps;

    @ColumnInfo(name = "total_steps")
    public int totalSteps;

    public HelperStep(String date, int initialSteps, int totalSteps) {
        this.date = date;
        this.initialSteps = initialSteps;
        this.totalSteps = totalSteps;
    }
}
