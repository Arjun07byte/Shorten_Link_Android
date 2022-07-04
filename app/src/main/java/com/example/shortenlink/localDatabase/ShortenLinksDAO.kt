package com.example.shortenlink.localDatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shortenlink.models.ShortenUrl

@Dao
interface ShortenLinksDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shortenLink: ShortenUrl): Long

    @Query("SELECT * FROM ShortenUrlTable")
    fun getAllLinks() : LiveData<List<ShortenUrl>>
}