package com.example.tabatatimer.model.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.tabatatimer.model.room.entities.SequenceDbEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SequenceDao {
    @Query("SELECT * FROM sequences WHERE id=:id ")
    fun getById(id: Long): SequenceDbEntity

    @Update(entity=SequenceDbEntity::class)
    suspend fun updateSequence(sequence: SequenceDbEntity)

    @Insert(entity = SequenceDbEntity::class)
    suspend fun createSequence(sequenceDbEntity: SequenceDbEntity)

    @Query("SELECT * FROM sequences ORDER BY id ASC")
    fun readAllData(): LiveData<List<SequenceDbEntity>>

    @Delete(entity=SequenceDbEntity::class)
    suspend fun deleteSequence(sequence: SequenceDbEntity)


}
