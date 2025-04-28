package com.swu.myapplication.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.swu.myapplication.data.entity.Timer

@Dao
interface TimerDao {
    @Query("SELECT * FROM timers ORDER BY createdTime DESC")
    fun getAllTimers(): LiveData<List<Timer>>
    
    @Query("SELECT * FROM timers WHERE id = :timerId")
    fun getTimerById(timerId: Long): LiveData<Timer>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timer: Timer): Long
    
    @Update
    suspend fun update(timer: Timer)
    
    @Delete
    suspend fun delete(timer: Timer)
    
    @Query("DELETE FROM timers WHERE id = :timerId")
    suspend fun deleteById(timerId: Long)
} 