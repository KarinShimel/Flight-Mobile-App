package com.example.newflightmobileapp.roomDataBase


import androidx.room.Database
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase



@Database(
    entities = [adressAndTime::class],
    version = 1
)
abstract class adressAndTimeDatabase : RoomDatabase() {

    abstract fun getAdressAndTimeDao(): adressAndTimeDao

}