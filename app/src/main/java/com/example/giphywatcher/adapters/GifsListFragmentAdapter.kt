package com.example.giphywatcher.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.giphywatcher.R
import com.example.giphywatcher.databinding.ItemGifsListLayoutBinding
import com.example.giphywatcher.entity.GifContentModel

class GifsListFragmentAdapter(val context: Context,
                              private val itemClickListener: (GifContentModel) -> Unit
) : PagingDataAdapter<GifContentModel, GifsListFragmentAdapter.GifsListViewHolder>(DataDiffItemCallback) {

    class GifsListViewHolder(val itemBinding: ItemGifsListLayoutBinding) :
            RecyclerView.ViewHolder(itemBinding.root) {

                fun bind(context: Context, gifContentModel: GifContentModel, itemClickListener: (GifContentModel) -> Unit) {
                    if (!gifContentModel.url.isNullOrEmpty()) {
                        Glide.with(context).load(gifContentModel.url).into(itemBinding.imageView)
                    } else {
                        //todo added default image
                    }

                    itemBinding.imageView2.setOnClickListener {
                        itemClickListener(gifContentModel)
                    }
                }
            }

    override fun onBindViewHolder(holder: GifsListViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(context, it, itemClickListener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GifsListViewHolder(
        DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_gifs_list_layout, parent, false)
    )

    private object DataDiffItemCallback : DiffUtil.ItemCallback<GifContentModel>() {
        override fun areItemsTheSame(oldItem: GifContentModel, newItem: GifContentModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: GifContentModel,
            newItem: GifContentModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}