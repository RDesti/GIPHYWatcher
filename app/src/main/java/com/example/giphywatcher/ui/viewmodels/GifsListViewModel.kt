package com.example.giphywatcher.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.giphywatcher.constants.AppDefaultValues
import com.example.giphywatcher.constants.AppDefaultValues.LAST_QUERY_SCROLLED
import com.example.giphywatcher.constants.AppDefaultValues.LAST_SEARCH_QUERY
import com.example.giphywatcher.entity.GifContentModel
import com.example.giphywatcher.network.parseModels.Data
import com.example.giphywatcher.entity.UiAction
import com.example.giphywatcher.entity.UiState
import com.example.giphywatcher.usecases.ISearchGifsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GifsListViewModel @Inject constructor(
    private val searchGifUseCase: ISearchGifsUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val state: StateFlow<UiState>
    val accept: (UiAction) -> Unit

    private var newPagingSource: PagingSource<*, *>? = null

    init {
        val currentSearchKey: String = AppDefaultValues.CURRENT_SEARCH_KEY ?: AppDefaultValues.DEFAULT_SEARCH_KEY
        val initialQuery: String = savedStateHandle[LAST_SEARCH_QUERY] ?: currentSearchKey
        val lastQueryScrolled: String = savedStateHandle[LAST_QUERY_SCROLLED] ?: currentSearchKey
        val actionStateFlow = MutableSharedFlow<UiAction>()
        val searches = actionStateFlow
            .filterIsInstance<UiAction.Search>()
            .distinctUntilChanged()
            .onStart { emit(UiAction.Search(searchKey = initialQuery)) }
        val queriesScrolled = actionStateFlow
            .filterIsInstance<UiAction.Scroll>()
            .distinctUntilChanged()
            // This is shared to keep the flow "hot" while caching the last query scrolled,
            // otherwise each flatMapLatest invocation would lose the last query scrolled,
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onStart { emit(UiAction.Scroll(currentSearchKey = lastQueryScrolled)) }

        state = searches
            .flatMapLatest { search ->
                combine(
                    queriesScrolled,
                    searchGifByKey(searchKey = search.searchKey),
                    ::Pair
                )
                    .distinctUntilChangedBy { it.second }
                    .map { (scroll, pagingData) ->
                        UiState(
                            searchKey = search.searchKey,
                            pagingData = pagingData,
                            lastQueryScrolled = scroll.currentSearchKey,
                            // If the search query matches the scroll query, the user has scrolled
                            hasNotScrolledForCurrentSearch = search.searchKey != scroll.currentSearchKey
                        )
                    }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = UiState()
            )

        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }

    private fun searchGifByKey(searchKey: String): Flow<PagingData<GifContentModel>> {
        return newPager(searchKey).flow
            .map { pagingData -> pagingData.map { createTopContentModel(it) } }
            .cachedIn(viewModelScope)
    }

    private fun newPager(query: String): Pager<Int, Data> {
        return Pager(PagingConfig(5, enablePlaceholders = false)) {
            searchGifUseCase(query).also { newPagingSource = it }
        }
    }

    override fun onCleared() {
        savedStateHandle[LAST_SEARCH_QUERY] = state.value.searchKey
        savedStateHandle[LAST_QUERY_SCROLLED] = state.value.lastQueryScrolled
        super.onCleared()
    }

    private fun createTopContentModel(model: Data): GifContentModel {
        return GifContentModel(
            model.id,
            model.title,
            model.images?.original?.url
        )
    }
}