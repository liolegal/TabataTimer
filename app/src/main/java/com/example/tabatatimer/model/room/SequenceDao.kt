package com.example.tabatatimer.model.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.tabatatimer.model.entities.Sequence
import com.example.tabatatimer.model.room.entities.SequenceDbEntity
import com.example.tabatatimer.model.room.entities.SequenceUpdateTuple
import kotlinx.coroutines.flow.Flow

@Dao
interface SequenceDao {
    @Query("SELECT * FROM sequences WHERE id=:id ")
    fun getById(id: Long): Flow<SequenceDbEntity?>

    @Update(entity=SequenceDbEntity::class)
    suspend fun updateSequence(sequence: SequenceUpdateTuple)

    @Insert(entity = SequenceDbEntity::class)
    suspend fun createSequence(sequenceDbEntity: SequenceDbEntity)

    @Query("SELECT * FROM sequences ORDER BY id ASC")
    fun readAllData(): LiveData<List<SequenceDbEntity>>
}
