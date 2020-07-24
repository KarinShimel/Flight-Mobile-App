package com.example.newflightmobileapp.roomDataBase


import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "AdressTable")
data class adressAndTime(
    @PrimaryKey @NonNull
    @ColumnInfo(name = "url")
    val adress: String,
    @ColumnInfo(name = "number")
    val number: Int = 0

)