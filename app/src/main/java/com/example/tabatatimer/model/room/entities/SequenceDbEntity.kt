package com.example.tabatatimer.model.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.tabatatimer.model.entities.NewSequenceData
import com.example.tabatatimer.model.entities.Sequence

@Entity(
    tableName="sequences",
    indices=[
        Index("name",unique=true)
    ]
)
data class SequenceDbEntity (
    @PrimaryKey(autoGenerate = true) val id:Long,
    val name:String,
    val color:String,
    @ColumnInfo(name="warmup_time") val warmUpTime:Long,
    @ColumnInfo(name="workout_time") val workoutTime:Long,
    @ColumnInfo(name="rest_time") val restTime:Long,
    val rounds:Int,
    val cycles:Int
    ){
    fun toSequence():Sequence=Sequence(
        id=id,
        name=name,
        color=color,
        warmUpTime=warmUpTime,
        workoutTime=workoutTime,
        restTime=restTime,
        rounds=rounds,
        cycles=cycles
    )
    companion object{
        fun fromSequenceData(newSequenceData: NewSequenceData): SequenceDbEntity = SequenceDbEntity(
            id=0,
            name=newSequenceData.name,
            color=newSequenceData.color,
            warmUpTime=newSequenceData.warmUpTime,
            workoutTime=newSequenceData.workoutTime,
            restTime=newSequenceData.restTime,
            rounds=newSequenceData.rounds,
            cycles=newSequenceData.cycles
        )
    }
}