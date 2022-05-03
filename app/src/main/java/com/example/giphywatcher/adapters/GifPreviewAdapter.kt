package com.example.giphywatcher.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.giphywatcher.databinding.ItemPreviewGifBinding
import com.example.giphywatcher.entity.GifContentModel

class GifPreviewAdapter : PagingDataAdapter<GifContentModel, GifPreviewAdapter.GifPreviewViewHolder>(
    GifsListFragmentAdapter.DataDiffItemCallback
) {

    class GifPreviewViewHolder(val itemBinding: ItemPreviewGifBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(gifContentModel: GifContentModel) {
            if (!gifContentModel.url.isNullOrEmpty()) {
                Glide.with(itemBinding.root).load(gifContentModel.url).into(itemBinding.imagePreview)
            } else {
                //todo added default image
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = GifPreviewViewHolder(ItemPreviewGifBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: GifPreviewViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}