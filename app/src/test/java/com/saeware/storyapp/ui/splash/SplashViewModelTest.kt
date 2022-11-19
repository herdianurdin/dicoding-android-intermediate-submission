package com.saeware.storyapp.ui.splash

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.saeware.storyapp.data.AuthRepository
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
class SplashViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock private lateinit var authRepository: AuthRepository

    private lateinit var splashViewModel: SplashViewModel

    private val dummyToken = DataDummy.DUMMY_TOKEN

    @Before
    fun setup() { splashViewModel = SplashViewModel(authRepository) }

    @Test
    fun `Get auth token empty`() = runTest {
        val expectedToken = MutableLiveData<String>()
        expectedToken.value = null
        `when`(authRepository.getAuthToken()).thenReturn(expectedToken)

        val actualToken = splashViewModel.getAuthToken().getOrAwaitValue()
        Mockito.verify(authRepository).getAuthToken()

        Assert.assertNull(actualToken)
    }

    @Test
    fun `Get auth token successfully`() = runTest {
        val expectedToken = MutableLiveData<String>()
        expectedToken.value = dummyToken
        `when`(authRepository.getAuthToken()).thenReturn(expectedToken)

        val actualToken = splashViewModel.getAuthToken().getOrAwaitValue()
        Mockito.verify(authRepository).getAuthToken()
        Assert.assertEquals(dummyToken, actualToken)
    }
}