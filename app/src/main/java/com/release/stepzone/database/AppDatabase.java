package com.release.stepzone.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {HelperStep.class, User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract HelperStepDao helperStepDao();
    public abstract UserDao userDao();
}
