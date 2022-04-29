package com.example.giphywatcher.repositories

import androidx.paging.PagingData
import com.example.giphywatcher.network.parseModels.Data
import kotlinx.coroutines.flow.Flow

interface IGiphyDataRepository {
    fun getDataFromGiphy(): Flow<PagingData<Data>>
}