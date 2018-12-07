package com.jaisel.tictactoe.Utils;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserDao {

    @Query("SELECT * FROM User;")
    List<User> getFriends();

    @Query("SELECT * FROM User WHERE id = :id")
    User getUser(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Delete
    void delete(User user);

    @Query("DELETE FROM User;")
    void deleteAll();
}
