package com.saeware.storyapp.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.saeware.storyapp.data.local.entity.RemoteKeys
import com.saeware.storyapp.data.local.entity.Story
import com.saeware.storyapp.data.local.entity.StoryDatabase
import com.saeware.storyapp.data.remote.retrofit.ApiService
import com.saeware.storyapp.utils.wrapEspressoIdlingResource

@ExperimentalPagingApi
class StoryRemoteMediator(
    private val token: String,
    private val apiService: ApiService,
    private val database: StoryDatabase
) : RemoteMediator<Int, Story>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Story>
    ): MediatorResult {
        val page = when(loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeysForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }
        }

        return wrapEspressoIdlingResource {
            try {
                val responseData = apiService.getAllStories(token, page, state.config.pageSize)
                val endOfPaginationReached = responseData.listStory.isEmpty()

                database.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        database.remoteKeysDao().removeAllRemoteKeys()
                        database.storyDao().removeAllStories()
                    }

                    val prevKey = if (page == 1) null else page + 1
                    val nextKey = if (endOfPaginationReached) null else page + 1
                    val keys = responseData.listStory.map {
                        RemoteKeys(id = it.id, prevKey, nextKey)
                    }

                    database.remoteKeysDao().addAllRemoteKeys(keys)

                    responseData.listStory.forEach { storyItem ->
                        val story = Story(
                            storyItem.id,
                            storyItem.name,
                            storyItem.description,
                            storyItem.createdAt,
                            storyItem.photoUrl,
                            storyItem.lon,
                            storyItem.lat
                        )

                        database.storyDao().addStory(story)
                    }

                    return@withTransaction MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
                }
            } catch (e: Exception) {
                return MediatorResult.Error(e)
            }
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Story>) =
        state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let {
                database.remoteKeysDao().getRemoteKeysId(it)
            }
        }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Story>) =
        state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }

    private suspend fun getRemoteKeysForLastItem(state: PagingState<Int, Story>) =
        state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}