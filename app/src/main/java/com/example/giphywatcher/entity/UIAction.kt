package com.example.giphywatcher.entity

sealed class UiAction {
    data class Search(val searchKey: String) : UiAction()
    data class Scroll(val currentSearchKey: String) : UiAction()
}
