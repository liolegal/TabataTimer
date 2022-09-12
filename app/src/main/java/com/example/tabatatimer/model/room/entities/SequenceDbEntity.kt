package com.example.tabatatimer.model.room.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
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
    ):Parcelable