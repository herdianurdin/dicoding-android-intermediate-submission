package com.saeware.storyapp.ui.post

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import com.saeware.storyapp.data.StoryRepository
import com.saeware.storyapp.data.remote.response.PostResponse
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
class PostViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock private lateinit var storyRepository: StoryRepository

    private lateinit var postViewModel: PostViewModel

    private val dummyToken = DataDummy.DUMMY_TOKEN
    private val dummyMultipartFile = DataDummy.generateDummyMultipartFile()
    private val dummyDescription = DataDummy.generateDummyRequestBody()
    private val dummyPostResponse = DataDummy.generateDummyPostResponse()

    @Before
    fun setup() { postViewModel = PostViewModel(storyRepository) }

    @Test
    fun `Post story failed`() = runTest {
        val expectedResponse = MutableLiveData<Result<PostResponse>>()
        expectedResponse.value = Result.failure(Exception("failed"))
        `when`(storyRepository.postStory(
            dummyToken, dummyMultipartFile, dummyDescription, null, null
        )).thenReturn(expectedResponse)

        val actualResponse = postViewModel
            .postStory(dummyToken, dummyMultipartFile, dummyDescription, null, null)
            .getOrAwaitValue()
        Mockito.verify(storyRepository)
            .postStory(dummyToken, dummyMultipartFile, dummyDescription, null, null)

        Assert.assertTrue(actualResponse.isFailure)
        Assert.assertFalse(actualResponse.isSuccess)
        actualResponse.onFailure { Assert.assertNotNull(it) }
    }

    @Test
    fun `Post story success`() = runTest {
        val expectedResponse = MutableLiveData<Result<PostResponse>>()
        expectedResponse.value = Result.success(dummyPostResponse)
        `when`(
            storyRepository.postStory(dummyToken, dummyMultipartFile, dummyDescription, null, null)
        ).thenReturn(expectedResponse)

        val actualResponse = postViewModel
            .postStory(dummyToken, dummyMultipartFile, dummyDescription, null, null)
            .getOrAwaitValue()
        Mockito.verify(storyRepository)
            .postStory(dummyToken, dummyMultipartFile, dummyDescription, null, null)

        Assert.assertTrue(actualResponse.isSuccess)
        Assert.assertFalse(actualResponse.isFailure)
        actualResponse.onSuccess {
            Assert.assertNotNull(it)
            Assert.assertSame(dummyPostResponse, it)
        }
    }
}