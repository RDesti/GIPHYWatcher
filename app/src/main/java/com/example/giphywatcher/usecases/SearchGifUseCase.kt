package com.example.giphywatcher.usecases

import androidx.paging.PagingSource
import com.example.giphywatcher.network.parseModels.Data
import com.example.giphywatcher.repositories.IGiphyDataRepository
import javax.inject.Inject

class SearchGifUseCase @Inject constructor(
    private val giphyDataRepository: IGiphyDataRepository
) : ISearchGifsUseCase{

    override operator fun invoke(searchKey: String): PagingSource<Int, Data> {
        return giphyDataRepository.getDataFromGiphy(searchKey)
    }
}