package com.example.giphywatcher.ui.fragments

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giphywatcher.R
import com.example.giphywatcher.adapters.GifListLoadStateAdapter
import com.example.giphywatcher.adapters.GifsListFragmentAdapter
import com.example.giphywatcher.constants.AppDefaultValues
import com.example.giphywatcher.databinding.FragmentGifsListBinding
import com.example.giphywatcher.entity.GifContentModel
import com.example.giphywatcher.entity.UiAction
import com.example.giphywatcher.entity.UiState
import com.example.giphywatcher.ui.viewmodels.GifsListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GifsListFragment : Fragment() {

    private var _binding: FragmentGifsListBinding? = null
    private val binding get() = _binding!!

    private val _viewModel by lazy { ViewModelProvider(this)[GifsListViewModel::class.java] }
    private var _adapter: GifsListFragmentAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gifs_list, container, false)
        binding.lifecycleOwner = this

        // bind the state
        bindState(
            uiState = _viewModel.state,
            pagingData = _viewModel.pagingDataFlow,
            uiActions = _viewModel.accept
        )

        return binding.root
    }

    private fun bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<GifContentModel>>,
        uiActions: (UiAction) -> Unit
    ) {
        initAdapter()
        bindSearch(
            uiState = uiState,
            onSearchKeyChanged = uiActions
        )

        _adapter?.let {
            bindList(
                gifsListAdapter = it,
                uiState = uiState,
                pagingData = pagingData,
                onScrollChanged = uiActions
            )
        }
    }

    private fun bindSearch(
        uiState: StateFlow<UiState>,
        onSearchKeyChanged: (UiAction.Search) -> Unit
    ) {
        binding.searchView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateGifsListFromInput(onSearchKeyChanged)
                true
            } else {
                false
            }
        }
        binding.searchView.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateGifsListFromInput(onSearchKeyChanged)
                true
            } else {
                false
            }
        }

        lifecycleScope.launch {
            uiState
                .map { it.searchKey }
                .distinctUntilChanged()
                .collect(binding.searchView::setText)
        }
    }

    private fun updateGifsListFromInput(onSearchKeyChanged: (UiAction.Search) -> Unit) {
        binding.searchView.text.trim().let {
            if (it.isNotEmpty()) {
                binding.recycler.scrollToPosition(0)
                onSearchKeyChanged(UiAction.Search(searchKey = it.toString()))
                AppDefaultValues.CURRENT_SEARCH_KEY = it.toString()
            }
        }
    }

    private fun bindList(
        gifsListAdapter: GifsListFragmentAdapter,
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<GifContentModel>>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {
        binding.recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentSearchKey = uiState.value.searchKey))
            }
        })
        val notLoading = gifsListAdapter.loadStateFlow
            // Only emit when REFRESH LoadState for RemoteMediator changes.
            .distinctUntilChangedBy { it.refresh }
            // Only react to cases where Remote REFRESH completes i.e., NotLoading.
            .map { it.refresh is LoadState.NotLoading }

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        )
            .distinctUntilChanged()

        lifecycleScope.launch {
            pagingData.collectLatest {
                _adapter?.submitData(it)
            }
        }

        lifecycleScope.launch {
            shouldScrollToTop
                .collect { shouldScroll ->
                    if (shouldScroll) binding.recycler.scrollToPosition(0)
                }
        }
    }

    private fun initAdapter() {
        _adapter = GifsListFragmentAdapter { model, position -> clickOnItem(model, position) }
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = _adapter?.withLoadStateHeaderAndFooter(
            header = GifListLoadStateAdapter { _adapter?.retry() },
            footer = GifListLoadStateAdapter { _adapter?.retry() }
        )
    }

    private fun clickOnItem(model: GifContentModel, position: Int) {
        //todo
        val bundle = bundleOf(
            "url" to model.url,
            "position" to position
        )
        this.findNavController()
            .navigate(R.id.action_gifsListFragment_to_gifPreviewFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _adapter = null
    }
}