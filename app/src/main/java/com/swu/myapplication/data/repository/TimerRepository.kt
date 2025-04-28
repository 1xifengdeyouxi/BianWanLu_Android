package com.swu.myapplication.data.repository

import androidx.lifecycle.LiveData
import com.swu.myapplication.data.dao.TimerDao
import com.swu.myapplication.data.entity.Timer

class TimerRepository(private val timerDao: TimerDao) {
    
    val allTimers: LiveData<List<Timer>> = timerDao.getAllTimers()
    
    fun getTimerById(timerId: Long): LiveData<Timer> {
        return timerDao.getTimerById(timerId)
    }
    
    suspend fun insert(timer: Timer): Long {
        return timerDao.insert(timer)
    }
    
    suspend fun update(timer: Timer) {
        timerDao.update(timer)
    }
    
    suspend fun delete(timer: Timer) {
        timerDao.delete(timer)
    }
    
    suspend fun deleteById(timerId: Long) {
        timerDao.deleteById(timerId)
    }
} 