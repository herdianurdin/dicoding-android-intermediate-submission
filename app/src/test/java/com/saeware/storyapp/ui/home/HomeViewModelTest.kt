package com.saeware.storyapp.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.saeware.storyapp.adapter.StoryAdapter
import com.saeware.storyapp.data.StoryRepository
import com.saeware.storyapp.data.local.entity.Story
import com.saeware.storyapp.utils.CoroutineTestUtil
import com.saeware.storyapp.utils.DataDummy
import com.saeware.storyapp.utils.PagingSourceTest
import com.saeware.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
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
class HomeViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestUtil = CoroutineTestUtil()

    @Mock private lateinit var storyRepository: StoryRepository

    private lateinit var homeViewModel: HomeViewModel

    @Before
    fun setup() { homeViewModel = HomeViewModel(storyRepository) }

    private val dummyToken = DataDummy.DUMMY_TOKEN
    private val dummyStories = DataDummy.generateDummyStories()
    private val listUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

    @Test
    fun `Get all stories`() = runTest {
        val data = PagingSourceTest.snapshot(dummyStories)
        val stories = MutableLiveData<PagingData<Story>>()
        stories.value = data
        `when`(storyRepository.getAllStories(dummyToken)).thenReturn(stories)

        val actualStories = homeViewModel.getAllStories(dummyToken).getOrAwaitValue()
        Mockito.verify(storyRepository).getAllStories(dummyToken)

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DiffUtilCallback,
            updateCallback = listUpdateCallback,
            mainDispatcher = coroutineTestUtil.testDispatcher,
            workerDispatcher = coroutineTestUtil.testDispatcher
        )
        differ.submitData(actualStories)
        advanceUntilIdle()

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
    }
}