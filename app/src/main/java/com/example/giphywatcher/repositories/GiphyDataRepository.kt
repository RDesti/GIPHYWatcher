package com.example.giphywatcher.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.giphywatcher.network.parseModels.Data
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GiphyDataRepository @Inject constructor(
    private val giphyPagingSource: GiphyPagingSource
) : IGiphyDataRepository {
    override fun getDataFromGiphy(): Flow<PagingData<Data>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {giphyPagingSource}
        ).flow
    }
}