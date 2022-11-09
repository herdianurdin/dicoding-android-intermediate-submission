package com.saeware.storyapp.data

import com.saeware.storyapp.data.local.AuthPreferences
import com.saeware.storyapp.data.remote.response.LoginResponse
import com.saeware.storyapp.data.remote.response.RegisterResponse
import com.saeware.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences
){
    suspend fun userLogin(
        email: String,
        password: String
    ): Flow<Result<LoginResponse>> = flow {
        try {
            val response = apiService.userLogin(email, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun userRegister(
        name: String,
        email: String,
        password: String
    ): Flow<Result<RegisterResponse>> = flow {
        try {
            val response = apiService.userRegister(name, email, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun storeAuthToken(token: String) { authPreferences.storeAuthToken(token) }

    fun getAuthToken(): Flow<String?> = authPreferences.getAuthToken()
}