package com.saeware.storyapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.saeware.storyapp.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    suspend fun userLogin(email: String, password: String) =
        authRepository.userLogin(email, password).asLiveData()

    fun storeAuthToken(token: String) {
        viewModelScope.launch {
            authRepository.storeAuthToken(token)
        }
    }
}