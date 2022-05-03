package com.example.giphywatcher.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.giphywatcher.database.dao.GiphyDataDao
import com.example.giphywatcher.database.dao.RemoteKeysDao
import com.example.giphywatcher.network.parseModels.Data

@Database(
    entities = [Data::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {

    abstract fun giphyDataDao(): GiphyDataDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDataBase(context).also { INSTANCE = it }
            }

        private fun buildDataBase(context: Context) =
            Room.databaseBuilder(context.applicationContext, AppDataBase::class.java, "GiphyDB")
                .build()
    }
}