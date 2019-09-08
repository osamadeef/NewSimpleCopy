package com.example.simplecopy.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NumbersDao {

    @Query("SELECT * FROM numbers")
    LiveData<List<Numbers>> loadAllTasks();

    @Query ("SELECT * FROM numbers WHERE title LIKE :searchQuery OR number LIKE :searchQuery")
    LiveData<List<Numbers>> searchFor (String searchQuery);

    @Insert
    void insertTask(Numbers numbers);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(Numbers numbers);

    @Delete
    void deleteTask(Numbers numbers);

    @Query ("DELETE FROM numbers")
    void deleteAllNumbers();

    @Query ("SELECT * FROM numbers WHERE id = :id")
    LiveData<Numbers> loadNumberById(int id);

}