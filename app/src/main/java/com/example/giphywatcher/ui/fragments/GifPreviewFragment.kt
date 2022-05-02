package com.example.giphywatcher.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.giphywatcher.R
import com.example.giphywatcher.adapters.GifPreviewAdapter
import com.example.giphywatcher.databinding.FragmentGifPreviewBinding
import com.example.giphywatcher.ui.viewmodels.GifsListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class GifPreviewFragment : Fragment() {

    private var _binding: FragmentGifPreviewBinding? = null
    private val binding get() = _binding!!
    private var currentPage: Int = 0

    private val _viewModel by lazy { ViewModelProvider(this)[GifsListViewModel::class.java] }
    private var _adapter: GifPreviewAdapter? = null

    private var isFirstOpen = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gif_preview, container, false)
        binding.lifecycleOwner = this
        currentPage = arguments?.get("position") as Int
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _adapter = GifPreviewAdapter()

        binding.pagerGifs.apply {
            adapter = _adapter
            registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (isFirstOpen) {
                        currentItem = currentPage
                        isFirstOpen = false
                    } else {
                        currentPage = position
                    }
                }
            })
        }

        lifecycleScope.launchWhenCreated {
            _viewModel.state.collectLatest {
                _adapter?.submitData(it.pagingData)
            }
        }

        initListeners()
    }

    private fun initListeners() {
        binding.imageBack.setOnClickListener {
            this.findNavController().navigateUp()
        }

        binding.imageDelete.setOnClickListener {
            //todo
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}