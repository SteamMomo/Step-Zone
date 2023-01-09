package com.release.stepzone;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class HelperStep {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "initial_steps")
    public int initialSteps;

    public HelperStep(String date, int initialSteps) {
        this.date = date;
        this.initialSteps = initialSteps;
    }
}
