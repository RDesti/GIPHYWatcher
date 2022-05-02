package com.example.giphywatcher.repositories

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.example.giphywatcher.network.parseModels.Data
import kotlinx.coroutines.flow.Flow

interface IGiphyDataRepository {
    fun getDataFromGiphy(searchKey: String): PagingSource<Int, Data>
}