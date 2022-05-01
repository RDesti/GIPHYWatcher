package com.example.giphywatcher.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.giphywatcher.entity.GifContentModel
import com.example.giphywatcher.network.parseModels.Data
import com.example.giphywatcher.repositories.IGiphyDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class GifsListViewModel @Inject constructor(
    private val giphyDataRepository: IGiphyDataRepository
) : ViewModel(){
    private var contentPagingData: Flow<PagingData<GifContentModel>>? = null

    fun getData(): Flow<PagingData<GifContentModel>> {
        //if (contentPagingData == null) {
            contentPagingData = giphyDataRepository.getDataFromGiphy()
                .map { pagingData -> pagingData.map { createTopContentModel(it) } }
                .cachedIn(viewModelScope)
        //}
        return contentPagingData as Flow<PagingData<GifContentModel>>
    }

    private fun createTopContentModel(model: Data): GifContentModel {
        return GifContentModel(
            model.id,
            model.title,
            model.images?.original?.url
        )
    }
}