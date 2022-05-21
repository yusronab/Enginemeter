package com.example.enginemeter.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.enginemeter.model.MainModel

@Database(
    entities = [MainModel::class],
    version = 1
)

abstract class Database : RoomDatabase() {

    abstract fun entityDao() : Dao
    companion object {
        @Volatile private var instance: com.example.enginemeter.room.Database? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            com.example.enginemeter.room.Database::class.java,
            "app_with_room.db"
        ).build()
    }

}