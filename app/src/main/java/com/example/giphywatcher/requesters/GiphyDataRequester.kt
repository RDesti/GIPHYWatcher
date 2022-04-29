package com.example.giphywatcher.requesters

import com.example.giphywatcher.constants.AppDefaultValues
import com.example.giphywatcher.network.api.GiphyApiService
import com.example.giphywatcher.network.parseModels.DataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

class GiphyDataRequester @Inject constructor(
    private var giphyApiService: GiphyApiService
) : IGiphyDataRequester {
    override suspend fun sendRequest(searchKey: String, offset: Int): Response<DataModel>? {
        return try {
            withContext(Dispatchers.IO) {
                    val url = "https://api.giphy.com/v1/gifs/search?api_key=YGHnKKBGSydS6nSt6WA\n" +
                            "oUcICWwmgCfvL&amp;q=${searchKey}&amp;limit=${AppDefaultValues.DEFAULT_LOAD_ITEMS_LIMIT}&amp;offset=${offset}&amp;rating=g&amp;lang=en"
                    return@withContext giphyApiService.getGiphyDataBySearch(url)
            }
        } catch (e: Exception) {
            return e.message?.toResponseBody()?.let { Response.error(e.hashCode(), it) }
        }
    }
}