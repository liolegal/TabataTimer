package com.example.tabatatimer.repository

import androidx.lifecycle.LiveData
import com.example.tabatatimer.model.room.SequenceDao
import com.example.tabatatimer.model.room.entities.SequenceDbEntity

class RoomSequenceRepository(private val sequenceDao: SequenceDao) {
    val readAllData: LiveData<List<SequenceDbEntity>> = sequenceDao.readAllData()
    suspend fun addSequence(sequenceDbEntity: SequenceDbEntity){
        sequenceDao.createSequence(sequenceDbEntity)
    }

}