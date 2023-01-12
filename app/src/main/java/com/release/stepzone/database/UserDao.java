package com.release.stepzone.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM User WHERE user_name LIKE :userName LIMIT 1")
    User findByName(String userName);

    @Query("SELECT * FROM user")
    List<User> getAll();

    @Update
    void update(User user);

    @Insert
    void insert(User user);

    @Delete
    void delete(User user);
}
