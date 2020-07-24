package com.example.newflightmobileapp.roomDataBase


import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface adressAndTimeDao {
    @Query("SELECT * FROM adresstable ")
    suspend fun getall(): List<adressAndTime>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUrl(adressAndTime: adressAndTime)

    @Query("DELETE FROM adresstable")
    suspend fun deleteAll();

    @Query("SELECT * FROM adresstable ORDER BY number ASC LIMIT 5")
    suspend fun getAllUrls(): List<adressAndTime>

    //after every insertion, we must call this function in order keep LRU cache architecture
    @Query("UPDATE adresstable SET number = number + 1")
    suspend fun updateNumbers()
}
