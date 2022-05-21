package com.example.enginemeter.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.enginemeter.model.MainModel

@Dao
interface Dao {

    @Insert
    suspend fun createData(entity: MainModel)

    @Query("SELECT * FROM MainModel")
    suspend fun getAllData() : MutableList<MainModel>

    @Query("SELECT * FROM MainModel WHERE id =:entity_id")
    suspend fun getDataById(entity_id: Int) : MutableList<MainModel>

    @Delete
    suspend fun deleteData(entity: MainModel)

}