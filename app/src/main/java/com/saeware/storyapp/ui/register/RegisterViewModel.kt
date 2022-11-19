package com.saeware.storyapp.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.saeware.storyapp.data.AuthRepository
import com.saeware.storyapp.data.remote.response.RegisterResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    suspend fun userRegister(
        name: String, email: String, password: String
    ): LiveData<Result<RegisterResponse>> = authRepository.userRegister(name, email, password)
}