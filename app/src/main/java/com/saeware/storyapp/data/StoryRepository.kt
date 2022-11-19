package com.saeware.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.saeware.storyapp.data.local.entity.Story
import com.saeware.storyapp.data.local.entity.StoryDatabase
import com.saeware.storyapp.data.remote.StoryRemoteMediator
import com.saeware.storyapp.data.remote.response.PostResponse
import com.saeware.storyapp.data.remote.response.StoriesResponse
import com.saeware.storyapp.data.remote.retrofit.ApiService
import com.saeware.storyapp.utils.wrapEspressoIdlingResource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalPagingApi
class StoryRepository @Inject constructor(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase
){
    fun getAllStories(token: String): LiveData<PagingData<Story>> =
        Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            remoteMediator = StoryRemoteMediator(
                generateBearerToken(token),
                apiService,
                storyDatabase
            ),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        ).liveData

    fun getAllStoriesWithLocation(token: String): LiveData<Result<StoriesResponse>> = liveData {
        wrapEspressoIdlingResource {
            try {
                val bearerToken = generateBearerToken(token)
                val response = apiService.getAllStories(bearerToken, size = 30, location = 1)
                emit(Result.success(response))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Result.failure(e))
            }
        }
    }

    suspend fun postStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ): LiveData<Result<PostResponse>> = liveData {
        try {
            val bearerToken = generateBearerToken(token)
            val response = apiService.postStory(bearerToken, file, description, lat, lon)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

    private fun generateBearerToken(token: String): String = "Bearer $token"
}