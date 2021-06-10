package com.shumikhin.rainandsun.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long, //id будет генерироваться автоматически из за анотации @PrimaryKey(autoGenerate = true)

    val city: String,
    val temperature: Int,
    val condition: String
)
