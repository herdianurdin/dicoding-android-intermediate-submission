package com.saeware.storyapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saeware.storyapp.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    fun storeAuthToken(token: String) {
        viewModelScope.launch { authRepository.storeAuthToken(token) }
    }
}