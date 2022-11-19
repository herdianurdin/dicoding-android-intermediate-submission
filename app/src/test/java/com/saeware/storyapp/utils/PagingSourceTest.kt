package com.saeware.storyapp.utils

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.saeware.storyapp.data.local.entity.Story

class PagingSourceTest : PagingSource<Int, LiveData<List<Story>>>() {
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<Story>>>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> =
        LoadResult.Page(emptyList(), 0, 1)

    companion object {
        fun snapshot(stories: List<Story>) = PagingData.from(stories)
    }
}