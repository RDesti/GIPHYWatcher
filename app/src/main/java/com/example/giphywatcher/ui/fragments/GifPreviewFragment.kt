package com.example.giphywatcher.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.giphywatcher.R
import com.example.giphywatcher.databinding.FragmentGifPreviewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GifPreviewFragment : Fragment() {

    private var _binding: FragmentGifPreviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gif_preview, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}