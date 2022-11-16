package com.example.tabatatimer.viewmodel

import android.app.Application
import android.database.sqlite.SQLiteException
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.tabatatimer.Constants.UNIQUE_EXEPTION
import com.example.tabatatimer.model.room.AppDatabase
import com.example.tabatatimer.repository.RoomSequenceRepository
import com.example.tabatatimer.model.room.entities.SequenceDbEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BaseViewModel(application: Application):AndroidViewModel(application) {
    val state=MutableLiveData("")
    val readAllData:LiveData<List<SequenceDbEntity>>
    private val repository: RoomSequenceRepository

    init{
        val sequenceDao=AppDatabase.getDatabase(application).sequenceDao()
        repository= RoomSequenceRepository(sequenceDao)
        readAllData=repository.readAllData
    }
    fun addSequence(sequenceDbEntity: SequenceDbEntity){
        viewModelScope.launch(Dispatchers.IO){
            try{
                repository.addSequence(sequenceDbEntity)
                state.postValue("Added")

            }catch(e: SQLiteException){
               state.postValue(UNIQUE_EXEPTION)
            }


        }
    }
    fun updateSequence(sequenceDbEntity: SequenceDbEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSequence(sequenceDbEntity)
        }
    }
    fun deleteSequence(sequenceDbEntity: SequenceDbEntity){
        viewModelScope.launch (Dispatchers.IO){
            repository.deleteSequence(sequenceDbEntity)
        }
    }
}