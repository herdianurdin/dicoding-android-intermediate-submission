package com.saeware.storyapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.saeware.storyapp.data.AuthRepository
import com.saeware.storyapp.data.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storyRepository: StoryRepository
): ViewModel() {
    fun storeAuthToken(token: String) {
        viewModelScope.launch { authRepository.storeAuthToken(token) }
    }

    suspend fun getAllStories(token: String) = storyRepository.getAllStories(token).asLiveData()
}