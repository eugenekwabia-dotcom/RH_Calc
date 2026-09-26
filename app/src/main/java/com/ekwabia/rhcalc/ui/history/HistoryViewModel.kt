package com.ekwabia.rhcalc.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ekwabia.rhcalc.data.AppDatabase
import com.ekwabia.rhcalc.data.CalculationRecord
import com.ekwabia.rhcalc.data.HistoryRepository
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HistoryRepository(AppDatabase.getInstance(application).calculationDao())

    val records: LiveData<List<CalculationRecord>> = repository.allRecords.asLiveData()

    fun delete(record: CalculationRecord) = viewModelScope.launch {
        repository.delete(record)
    }

    fun clearAll() = viewModelScope.launch {
        repository.clear()
    }
}
