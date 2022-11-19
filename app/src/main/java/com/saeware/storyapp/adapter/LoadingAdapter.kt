package com.saeware.storyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.saeware.storyapp.databinding.LayoutLoadingBinding
import com.saeware.storyapp.utils.ViewExtensions.setVisible

class LoadingAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<LoadingAdapter.LoadingViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingViewHolder {
        val binding =
            LayoutLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingViewHolder(binding, retry)
    }

    override fun onBindViewHolder(holder: LoadingViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    inner class LoadingViewHolder(
        private val binding: LayoutLoadingBinding,
        retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnRetry.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            binding.apply {
                progressBar.setVisible(loadState is LoadState.Loading)
                tvErrorResult.setVisible(loadState is LoadState.Error)
                btnRetry.setVisible(loadState is LoadState.Error)
            }
        }
    }
}