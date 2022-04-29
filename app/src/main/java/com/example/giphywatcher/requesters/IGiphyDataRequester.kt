package com.example.giphywatcher.requesters

import com.example.giphywatcher.network.parseModels.DataModel
import retrofit2.Response

interface IGiphyDataRequester {
    suspend fun sendRequest(searchKey: String, offset: Int): Response<DataModel>?
}