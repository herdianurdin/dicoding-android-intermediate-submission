package com.saeware.storyapp.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.saeware.storyapp.data.StoryRepository
import com.saeware.storyapp.data.remote.response.PostResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
@ExperimentalPagingApi
class PostViewModel @Inject constructor(
    private val storyRepository: StoryRepository
): ViewModel() {
    suspend fun postStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): LiveData<Result<PostResponse>> =
        storyRepository.postStory(token, file, description, lat, lon)
}