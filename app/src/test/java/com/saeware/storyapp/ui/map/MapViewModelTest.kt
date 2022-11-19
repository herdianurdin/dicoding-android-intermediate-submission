package com.saeware.storyapp.ui.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import com.saeware.storyapp.data.StoryRepository
import com.saeware.storyapp.data.remote.response.StoriesResponse
import com.saeware.storyapp.utils.DataDummy
import com.saeware.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
@ExperimentalPagingApi
class MapViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock private lateinit var storyRepository: StoryRepository

    private lateinit var mapViewModel: MapViewModel

    private val dummyStoriesResponse = DataDummy.generateDummyStoriesResponse()
    private val dummyToken = DataDummy.DUMMY_TOKEN

    @Before
    fun setup() { mapViewModel = MapViewModel(storyRepository) }

    @Test
    fun `Get story with location failed`() = runTest {
        val expectedResponse = MutableLiveData<Result<StoriesResponse>>()
        expectedResponse.value = Result.failure(Exception("failed"))
        `when`(storyRepository.getAllStoriesWithLocation(dummyToken)).thenReturn(expectedResponse)

        val actualResponse = mapViewModel.getAllStoriesWithLocation(dummyToken).getOrAwaitValue()
        Mockito.verify(storyRepository).getAllStoriesWithLocation(dummyToken)

        Assert.assertTrue(actualResponse.isFailure)
        Assert.assertFalse(actualResponse.isSuccess)
        actualResponse.onFailure { Assert.assertNotNull(it) }
    }

    @Test
    fun `Get story with location success`() = runTest {
        val expectedResponse = MutableLiveData<Result<StoriesResponse>>()
        expectedResponse.value = Result.success(dummyStoriesResponse)
        `when`(storyRepository.getAllStoriesWithLocation(dummyToken)).thenReturn(expectedResponse)

        val actualResponse = mapViewModel.getAllStoriesWithLocation(dummyToken).getOrAwaitValue()
        Mockito.verify(storyRepository).getAllStoriesWithLocation(dummyToken)

        Assert.assertTrue(actualResponse.isSuccess)
        Assert.assertFalse(actualResponse.isFailure)
        actualResponse.onSuccess {
            Assert.assertNotNull(it)
            Assert.assertSame(dummyStoriesResponse, it)
        }
    }
}