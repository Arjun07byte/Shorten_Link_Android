package com.example.shortenlink.localDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shortenlink.models.ShortenUrl

@Database(
    entities = [ShortenUrl::class],
    version = 1,
    exportSchema = false
)
abstract class ShortenLinkDatabase: RoomDatabase() {
    abstract fun getShortenLinkDAO(): ShortenLinksDAO

    companion object {
        @Volatile
        private var databaseInstance: ShortenLinkDatabase? = null
        private val myLock = Any()

        operator fun invoke(currContext: Context) = databaseInstance ?: synchronized((myLock)) {
            databaseInstance ?: createDatabase(currContext)
        }

        private fun createDatabase(currContext: Context) =
            Room.databaseBuilder(
                currContext.applicationContext,
                ShortenLinkDatabase::class.java,
                "shortLinks_db.db"
            ).build()
    }
}