package com.example.giphywatcher.network.api

import com.example.giphywatcher.network.parseModels.DataModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface GiphyApiService {
    @GET
    suspend fun getGiphyDataBySearch(@Url url: String): Response<DataModel>
}