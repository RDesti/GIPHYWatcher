package com.example.giphywatcher.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.example.giphywatcher.network.parseModels.Data
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GiphyDataRepository @Inject constructor(
    private val giphyPagingSource: GiphyPagingSource.Factory
) : IGiphyDataRepository {
    override fun getDataFromGiphy(searchKey: String): PagingSource<Int, Data> {
        return giphyPagingSource.create(searchKey)
    }
}