package com.example.tabatatimer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.tabatatimer.model.room.AppDatabase
import com.example.tabatatimer.repository.RoomSequenceRepository
import com.example.tabatatimer.model.room.entities.SequenceDbEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SequenceViewModel(application: Application):AndroidViewModel(application) {
    val readAllData:LiveData<List<SequenceDbEntity>>
    private val repository: RoomSequenceRepository

    init{
        val sequenceDao=AppDatabase.getDatabase(application).sequenceDao()
        repository= RoomSequenceRepository(sequenceDao)
        readAllData=repository.readAllData
    }
    fun addSequence(sequenceDbEntity: SequenceDbEntity){
        viewModelScope.launch(Dispatchers.IO){
            repository.addSequence(sequenceDbEntity)
        }
    }
}