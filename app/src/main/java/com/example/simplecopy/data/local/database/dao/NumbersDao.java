package com.example.simplecopy.data.local.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.simplecopy.data.model.Numbers;

import java.util.List;

@Dao
public interface NumbersDao {

    @Query("SELECT * FROM numbers ORDER BY favorite DESC")
    LiveData<List<Numbers>> loadAllTasks();

    @Query("SELECT * FROM numbers ORDER BY CASE WHEN daily = 0 THEN 1 ELSE 0 END, done, daily DESC ")
    LiveData<List<Numbers>> loadAllByDaily();

    @Query ("SELECT * FROM numbers WHERE title LIKE :searchQuery OR number LIKE :searchQuery ORDER BY favorite DESC")
    LiveData<List<Numbers>> searchFor (String searchQuery);

    @Query ("SELECT * FROM numbers WHERE title LIKE :searchQuery OR number LIKE :searchQuery ORDER BY CASE WHEN daily = 0 THEN 1 ELSE 0 END, done, daily DESC")
    LiveData<List<Numbers>> searchForByDaily (String searchQuery);

    @Query("SELECT SUM(daily) FROM numbers WHERE done = 0")
    LiveData<Integer> getTotalOFDaily();

    @Insert
    long insertTask(Numbers numbers);

    @Insert
    void insertTaskk(Numbers numbers);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(Numbers numbers);

    @Delete
    void deleteTask(Numbers numbers);

    @Query ("DELETE FROM numbers")
    void deleteAllNumbers();

    @Query ("SELECT * FROM numbers WHERE id = :id")
    LiveData<Numbers> loadNumberById(int id);

    @Query("UPDATE numbers SET favorite= :value WHERE id = :itemId")
    void insertFavorite(int value, int itemId);

    @Query("UPDATE numbers SET daily = :value WHERE id = :itemId")
    void insertDaily(int value, int itemId);

    @Query("SELECT daily FROM numbers WHERE id = :itemId")
    int getDailyDB(int itemId);

    @Query("UPDATE numbers SET done = :value WHERE id = :itemId")
    void insertIfDone(int value, int itemId);

    @Query("SELECT done FROM numbers WHERE id = :itemId")
    int getDoneDB(int itemId);

    @Query ("DELETE FROM numbers WHERE id IN (:ids)")
    void deleteItemByIds(List<Integer> ids);

    @Query("SELECT * FROM numbers WHERE title = :userName")
    boolean isNameExist(String userName);

    @Query("SELECT * FROM numbers WHERE id = :uid")
    boolean isIdExist(int uid);

    @Query("SELECT * FROM numbers WHERE title = :userName")
    int getIdAfterSave(String userName);
}
