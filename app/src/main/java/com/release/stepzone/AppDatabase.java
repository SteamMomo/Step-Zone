package com.release.stepzone;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {HelperStep.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract HelperStepDao helperStepDao();
}
