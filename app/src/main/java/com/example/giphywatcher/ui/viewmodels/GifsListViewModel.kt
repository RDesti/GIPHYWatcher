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
import com.example.giphywatcher.repositories.IGiphyDataRepository
import com.example.giphywatcher.usecases.ISearchGifsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GifsListViewModel @Inject constructor(
    private val searchGifUseCase: ISearchGifsUseCase,
    private val giphyDataRepository: IGiphyDataRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val pagingDataFlow: Flow<PagingData<GifContentModel>>
    val state: StateFlow<UiState>
    val accept: (UiAction) -> Unit

    private var newPagingSource: PagingSource<*, *>? = null

    init {
        val currentSearchKey: String =
            AppDefaultValues.CURRENT_SEARCH_KEY ?: AppDefaultValues.DEFAULT_SEARCH_KEY
        val initialQuery: String = savedStateHandle[LAST_SEARCH_QUERY] ?: currentSearchKey
        val lastQueryScrolled: String = savedStateHandle[LAST_QUERY_SCROLLED] ?: AppDefaultValues.DEFAULT_SEARCH_KEY
        val actionStateFlow = MutableSharedFlow<UiAction>()
        val searches = actionStateFlow
            .filterIsInstance<UiAction.Search>()
            .distinctUntilChanged()
            .onStart { emit(UiAction.Search(searchKey = initialQuery)) }
        val queriesScrolled = actionStateFlow
            .filterIsInstance<UiAction.Scroll>()
            .distinctUntilChanged()
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onStart { emit(UiAction.Scroll(currentSearchKey = lastQueryScrolled)) }

        pagingDataFlow = searches
            .flatMapLatest { searchGifByKeyFromMediator(searchKey = it.searchKey) }
            .cachedIn(viewModelScope)

        state = combine(
            searches,
            queriesScrolled,
            ::Pair
        )
            .map { (search, scroll) ->
                UiState(
                    searchKey = search.searchKey,
                    lastQueryScrolled = scroll.currentSearchKey,
                    // If the search query matches the scroll query, the user has scrolled
                    hasNotScrolledForCurrentSearch = search.searchKey != scroll.currentSearchKey
                )
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

    //working with RemoteMediator
    private fun searchGifByKeyFromMediator(searchKey: String): Flow<PagingData<GifContentModel>> {
        return giphyDataRepository.getSearchResultFromRemoteMediator(searchKey)
            .map { pagingData -> pagingData.map { createGifContentModel(it) } }
    }

    override fun onCleared() {
        savedStateHandle[LAST_SEARCH_QUERY] = state.value.searchKey
        savedStateHandle[LAST_QUERY_SCROLLED] = state.value.lastQueryScrolled
        super.onCleared()
    }

    private fun createGifContentModel(model: Data): GifContentModel {
        return GifContentModel(
            model.id,
            model.title,
            model.images?.original?.url
        )
    }

    //working with PagingSource
    private fun searchGifByKey(searchKey: String): Flow<PagingData<GifContentModel>> {
        return newPager(searchKey).flow
            .map { pagingData -> pagingData.map { createGifContentModel(it) } }
            .cachedIn(viewModelScope)
    }

    private fun newPager(query: String): Pager<Int, Data> {
        return Pager(PagingConfig(5, enablePlaceholders = false)) {
            searchGifUseCase(query).also { newPagingSource = it }
        }
    }
}