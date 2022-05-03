package com.example.giphywatcher.entity

import com.example.giphywatcher.constants.AppDefaultValues.DEFAULT_SEARCH_KEY

data class UiState(
    val searchKey: String = DEFAULT_SEARCH_KEY,
    val lastQueryScrolled: String = DEFAULT_SEARCH_KEY,
    val hasNotScrolledForCurrentSearch: Boolean = false
)