package com.example.giphywatcher.repositories

import androidx.paging.*
import com.example.giphywatcher.database.AppDataBase
import com.example.giphywatcher.network.parseModels.Data
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GiphyDataRepository @Inject constructor(
    private val giphyPagingSource: GiphyPagingSource.Factory,
    private val giphyRemoteMediator: GiphyRemoteMediator.Factory,
    private val dataBase: AppDataBase
) : IGiphyDataRepository {

    //working with pagingSource
    override fun getDataFromGiphy(searchKey: String): PagingSource<Int, Data> {
        return giphyPagingSource.create(searchKey)
    }

    //working with remoteMediator
    @OptIn(ExperimentalPagingApi::class)
    override fun getSearchResultFromRemoteMediator(searchKey: String): Flow<PagingData<Data>> {
        val key = "%${searchKey.replace(' ', '%')}%"
        return Pager(
            config = PagingConfig(pageSize = 5, enablePlaceholders = false),
            remoteMediator = giphyRemoteMediator.create(searchKey),
            pagingSourceFactory = { dataBase.giphyDataDao().getModelsBySearch(key) }
        ).flow
    }
}