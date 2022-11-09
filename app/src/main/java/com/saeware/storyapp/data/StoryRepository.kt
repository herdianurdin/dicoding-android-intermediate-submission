package com.saeware.storyapp.data

import com.saeware.storyapp.data.remote.response.PostResponse
import com.saeware.storyapp.data.remote.response.StoriesResponse
import com.saeware.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class StoryRepository @Inject constructor(
    private val apiService: ApiService
){
    suspend fun getAllStories(
        token: String,
        page: Int? = null,
        size: Int? = null
    ): Flow<Result<StoriesResponse>> = flow {
        try {
            val bearerToken = generateBearerToken(token)
            val response = apiService.getAllStories(bearerToken, page, size)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

    suspend fun postStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody
    ): Flow<Result<PostResponse>> = flow {
        try {
            val bearerToken = generateBearerToken(token)
            val response = apiService.postStory(bearerToken, file, description)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

    private fun generateBearerToken(token: String): String = "Bearer $token"
}