package com.saeware.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.saeware.storyapp.data.local.AuthPreferences
import com.saeware.storyapp.data.remote.response.LoginResponse
import com.saeware.storyapp.data.remote.response.RegisterResponse
import com.saeware.storyapp.data.remote.retrofit.ApiService
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences
){
    suspend fun userLogin(
        email: String,
        password: String
    ): LiveData<Result<LoginResponse>> = liveData {
        try {
            val response = apiService.userLogin(email, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

    suspend fun userRegister(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> = liveData {
        try {
            val response = apiService.userRegister(name, email, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

    suspend fun storeAuthToken(token: String) { authPreferences.storeAuthToken(token) }

    fun getAuthToken(): LiveData<String?> = authPreferences.getAuthToken()
}