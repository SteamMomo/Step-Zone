package com.release.stepzone.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HelperStepDao {

    @Query("SELECT * FROM HelperStep WHERE date LIKE :date LIMIT 1")
    HelperStep findByDate(String date);

    @Query("SELECT * FROM HelperStep")
    List<HelperStep> getAll();

    @Update
    void update(HelperStep helperStep);

    @Insert
    void insert(HelperStep helperStep);

    @Delete
    void delete(HelperStep helperStep);
}
