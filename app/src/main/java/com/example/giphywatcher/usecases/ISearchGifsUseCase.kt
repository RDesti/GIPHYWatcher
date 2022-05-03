package com.example.giphywatcher.usecases

import androidx.paging.PagingSource
import com.example.giphywatcher.network.parseModels.Data

interface ISearchGifsUseCase {
    operator fun invoke(searchKey: String): PagingSource<Int, Data>
}