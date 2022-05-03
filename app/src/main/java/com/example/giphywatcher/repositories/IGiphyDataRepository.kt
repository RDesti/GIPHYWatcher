package com.example.giphywatcher.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.RemoteMediator
import com.example.giphywatcher.network.parseModels.Data
import kotlinx.coroutines.flow.Flow

interface IGiphyDataRepository {
    fun getDataFromGiphy(searchKey: String): PagingSource<Int, Data>

    @OptIn(ExperimentalPagingApi::class)
    fun getSearchResultFromRemoteMediator(searchKey: String): Flow<PagingData<Data>>
}