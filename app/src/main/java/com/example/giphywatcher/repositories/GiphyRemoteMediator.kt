package com.example.giphywatcher.repositories

import androidx.paging.*
import androidx.room.withTransaction
import com.example.giphywatcher.constants.AppDefaultValues
import com.example.giphywatcher.database.AppDataBase
import com.example.giphywatcher.database.RemoteKeys
import com.example.giphywatcher.network.parseModels.Data
import com.example.giphywatcher.requesters.IGiphyDataRequester
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class GiphyRemoteMediator @AssistedInject constructor(
    private val giphyDataRequester: IGiphyDataRequester,
    @Assisted("searchKey") private val searchKey: String,
    private val appDataBase: AppDataBase
) : RemoteMediator<Int, Data>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, Data>): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: AppDefaultValues.GIPHY_STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        val offset = (page - 1) * AppDefaultValues.DEFAULT_LOAD_ITEMS_LIMIT

        try {
            val responseModel = giphyDataRequester.sendRequest(searchKey, offset)

            val items =
                responseModel?.body()?.data
                    ?: emptyList()

            val endOfPaginationReached = items.isEmpty()
            appDataBase.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    appDataBase.remoteKeysDao().clearRemoteKeys()
                    appDataBase.giphyDataDao().clearRepos()
                }
                val prevKey =
                    if (page == AppDefaultValues.GIPHY_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = items.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                appDataBase.remoteKeysDao().insertAll(keys)
                appDataBase.giphyDataDao().insertAll(items)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Data>): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { data ->
                // Get the remote keys of the last item retrieved
                data.id.let { appDataBase.remoteKeysDao().remoteKeysId(it) }
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Data>): RemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { data ->
                // Get the remote keys of the first items retrieved
                data.id.let { appDataBase.remoteKeysDao().remoteKeysId(it) }
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Data>
    ): RemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { dataId ->
                appDataBase.remoteKeysDao().remoteKeysId(dataId)
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("searchKey") searchKey: String): GiphyRemoteMediator
    }
}