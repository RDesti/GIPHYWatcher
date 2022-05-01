package com.example.giphywatcher.repositories

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.giphywatcher.constants.AppDefaultValues
import com.example.giphywatcher.network.parseModels.Data
import com.example.giphywatcher.requesters.IGiphyDataRequester
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GiphyPagingSource @Inject constructor(
    private val giphyDataRequester: IGiphyDataRequester,
    //private val searchKey: String
) : PagingSource<Int, Data>() {
    override fun getRefreshKey(state: PagingState<Int, Data>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Data> {
        val page = params.key ?: AppDefaultValues.GIPHY_STARTING_PAGE_INDEX
        val pageSize = params.loadSize
        val offset = (page - 1) * AppDefaultValues.DEFAULT_LOAD_ITEMS_LIMIT

        return try {
            val responseModel = giphyDataRequester.sendRequest(AppDefaultValues.searchCondition, offset)
            if (responseModel == null || !responseModel.isSuccessful) {
                return LoadResult.Error(HttpException(responseModel))
            } else {
                val items =
                    responseModel.body()?.data
                        ?: emptyList()
                val nextKey = if (items.size < pageSize) null else page + 1

                LoadResult.Page(
                    data = items,
                    prevKey = if (page == AppDefaultValues.GIPHY_STARTING_PAGE_INDEX) null else page - 1,
                    nextKey = nextKey
                )
            }

        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}