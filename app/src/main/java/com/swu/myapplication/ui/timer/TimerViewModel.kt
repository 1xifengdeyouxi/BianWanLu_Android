package com.swu.myapplication.ui.timer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.swu.myapplication.data.database.AppDatabase
import com.swu.myapplication.data.entity.Timer
import com.swu.myapplication.data.repository.TimerRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TimerViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: TimerRepository
    val allTimers: LiveData<List<Timer>>
    
    init {
        val timerDao = AppDatabase.getDatabase(application).timerDao()
        repository = TimerRepository(timerDao)
        allTimers = repository.allTimers
    }
    
    fun getTimerById(id: Long): LiveData<Timer> {
        return repository.getTimerById(id)
    }
    
    fun insert(timer: Timer) = viewModelScope.launch {
        repository.insert(timer)
    }
    
    fun update(timer: Timer) = viewModelScope.launch {
        repository.update(timer)
    }
    
    fun delete(timer: Timer) = viewModelScope.launch {
        repository.delete(timer)
    }
    
    fun deleteById(timerId: Long) = viewModelScope.launch {
        repository.deleteById(timerId)
    }
    
    /**
     * 创建新的计时器，并返回创建的ID
     */
    suspend fun createTimerAndGetId(title: String, durationMinutes: Int, atmosphereTitle: String? = null, atmosphereImageUri: String? = null): Long {
        return withContext(Dispatchers.IO) {
            val timer = Timer(
                title = title, 
                durationMinutes = durationMinutes,
                atmosphereTitle = atmosphereTitle,
                atmosphereImageUri = atmosphereImageUri
            )
            repository.insert(timer)
        }
    }
    
    /**
     * 创建新的计时器
     */
    fun createTimer(title: String, durationMinutes: Int, atmosphereTitle: String? = null, atmosphereImageUri: String? = null) = viewModelScope.launch {
        val timer = Timer(
            title = title, 
            durationMinutes = durationMinutes,
            atmosphereTitle = atmosphereTitle,
            atmosphereImageUri = atmosphereImageUri
        )
        repository.insert(timer)
    }
} 