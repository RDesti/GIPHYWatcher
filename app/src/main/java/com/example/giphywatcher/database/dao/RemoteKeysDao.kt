package com.example.giphywatcher.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.giphywatcher.database.RemoteKeys

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeys>)

    @Query("SELECT * FROM remoteKeys WHERE id = :keysId")
    suspend fun remoteKeysId(keysId: String): RemoteKeys?

    @Query("DELETE FROM remoteKeys")
    suspend fun clearRemoteKeys()
}