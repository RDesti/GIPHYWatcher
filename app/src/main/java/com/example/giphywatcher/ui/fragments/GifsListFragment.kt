package com.example.giphywatcher.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.giphywatcher.R
import com.example.giphywatcher.adapters.GifListLoadStateAdapter
import com.example.giphywatcher.adapters.GifsListFragmentAdapter
import com.example.giphywatcher.constants.AppDefaultValues
import com.example.giphywatcher.databinding.FragmentGifsListBinding
import com.example.giphywatcher.entity.GifContentModel
import com.example.giphywatcher.ui.viewmodels.GifsListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

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
        initAdapter()
        initSearch()
        return binding.root
    }

    private fun initAdapter() {
        _adapter = GifsListFragmentAdapter { model -> clickOnItem(model) }
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = _adapter?.withLoadStateHeaderAndFooter(
            header = GifListLoadStateAdapter { _adapter?.retry() },
            footer = GifListLoadStateAdapter { _adapter?.retry() }
        )
    }

    private fun clickOnItem(model: GifContentModel) {
    //todo
        val bundle = bundleOf("url" to model.url)
        this.findNavController()
            .navigate(R.id.action_gifsListFragment_to_gifPreviewFragment, bundle)
    }

    private fun initViewModel() {
        lifecycleScope.launchWhenCreated {
            _viewModel.getData().collectLatest {
                _adapter?.submitData(it)
            }
        }
    }

    private fun initSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (!p0.isNullOrBlank()) {
                    AppDefaultValues.searchCondition = p0
                    initViewModel()
                } else {
                    AppDefaultValues.searchCondition = ""
                    //todo warning message
                }

                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _adapter = null
    }
}