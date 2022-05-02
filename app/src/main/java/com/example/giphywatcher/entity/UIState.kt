package com.example.giphywatcher.entity

import androidx.paging.PagingData
import com.example.giphywatcher.constants.AppDefaultValues.DEFAULT_SEARCH_KEY

data class UiState(
    val searchKey: String = DEFAULT_SEARCH_KEY,
    val lastQueryScrolled: String = DEFAULT_SEARCH_KEY,
    val hasNotScrolledForCurrentSearch: Boolean = false,
    val pagingData: PagingData<GifContentModel> = PagingData.empty()
)