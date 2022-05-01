package com.example.giphywatcher.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.giphywatcher.R
import com.example.giphywatcher.databinding.ItemLoadStateFooterViewBinding

class GifListLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<GifListLoadStateAdapter.GifsLoadStateViewHolder>(){

    class GifsLoadStateViewHolder(
        val itemFooterBinding: ItemLoadStateFooterViewBinding,
        retry: () -> Unit
    ) : RecyclerView.ViewHolder(itemFooterBinding.root) {

        init {
            itemFooterBinding.errorText.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            itemFooterBinding.errorConstraint.isVisible = loadState is LoadState.Error
            itemFooterBinding.loadingAnimation.isVisible = loadState is LoadState.Loading
        }
    }

    override fun onBindViewHolder(
        holder: GifsLoadStateViewHolder,
        loadState: LoadState
    ) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): GifsLoadStateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_load_state_footer_view, parent, false)
        val binding = ItemLoadStateFooterViewBinding.bind(view)
        return GifsLoadStateViewHolder(binding, retry)
    }
}