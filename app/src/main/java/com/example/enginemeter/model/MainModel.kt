package com.example.enginemeter.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MainModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var speed: Double,
    var time: Double,
    var result: Double
)