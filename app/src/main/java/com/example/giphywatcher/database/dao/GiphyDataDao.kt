package com.example.giphywatcher.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.giphywatcher.entity.GifContentModel
import com.example.giphywatcher.network.parseModels.Data

@Dao
interface GiphyDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(models: List<Data>)

    @Query("SELECT * FROM giphyData WHERE title LIKE :searchKey OR url LIKE :searchKey")
    fun getModelsBySearch(searchKey: String): PagingSource<Int, Data>

    @Query("DELETE FROM giphyData")
    suspend fun clearRepos()

    @Query("DELETE FROM giphyData WHERE id = :modelId")
    fun deleteGifContentModel(modelId: String)
}