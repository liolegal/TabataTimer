package com.example.tabatatimer.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tabatatimer.model.room.entities.SequenceDbEntity

@Database(
    version = 1,
    entities = [
        SequenceDbEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sequenceDao(): SequenceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sequence_database"
                ).build()
                INSTANCE=instance
                return instance
            }

        }
    }
}