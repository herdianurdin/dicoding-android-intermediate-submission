package com.saeware.storyapp.ui.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.saeware.storyapp.data.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val storyRepository: StoryRepository
): ViewModel() {
    suspend fun postStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody
    ) = storyRepository.postStory(token, file, description).asLiveData()
}