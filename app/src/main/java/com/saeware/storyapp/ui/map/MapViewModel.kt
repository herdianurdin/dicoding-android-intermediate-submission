package com.saeware.storyapp.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.saeware.storyapp.data.StoryRepository
import com.saeware.storyapp.data.remote.response.StoriesResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
@ExperimentalPagingApi
class MapViewModel @Inject constructor(
    private val storyRepository: StoryRepository
) : ViewModel() {
    fun getAllStoriesWithLocation(token: String): LiveData<Result<StoriesResponse>> =
        storyRepository.getAllStoriesWithLocation(token)
}