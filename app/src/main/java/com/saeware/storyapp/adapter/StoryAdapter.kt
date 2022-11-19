package com.saeware.storyapp.adapter

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.saeware.storyapp.data.local.entity.Story
import com.saeware.storyapp.databinding.ItemStoryBinding
import com.saeware.storyapp.utils.ViewExtensions.setImageFromUrl
import com.saeware.storyapp.utils.ViewExtensions.setLocalDateFormat

class StoryAdapter :
    PagingDataAdapter<Story, StoryAdapter.ListViewHolder>(DiffUtilCallback)
{
    private lateinit var onStartActivityCallback: OnStartActivityCallback

    inner class ListViewHolder(private var binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, story: Story) {
            binding.apply {
                ivPhoto.setImageFromUrl(context, story.photoUrl)
                tvName.text = story.name
                tvDate.setLocalDateFormat(story.createdAt)
                tvDescription.text = story.description

                root.setOnClickListener {
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            root.context as Activity,
                            Pair(ivPhoto, "photo"),
                            Pair(tvName, "name"),
                            Pair(tvDate, "date"),
                            Pair(tvDescription, "description")
                        )

                    onStartActivityCallback.onStartActivityCallback(story, optionsCompat.toBundle())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(holder.itemView.context, story)
        }
    }

    fun setOnStartActivityCallback(onStartActivityCallback: OnStartActivityCallback) {
        this.onStartActivityCallback = onStartActivityCallback
    }

    interface OnStartActivityCallback {
        fun onStartActivityCallback(story: Story, bundle: Bundle?)
    }

    companion object {
        val DiffUtilCallback = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean =
                oldItem == newItem
        }
    }
}