package com.saeware.storyapp.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.saeware.storyapp.data.AuthRepository
import com.saeware.storyapp.data.remote.response.LoginResponse
import com.saeware.storyapp.utils.CoroutineTestUtil
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
class LoginViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestUtil = CoroutineTestUtil()

    @Mock private lateinit var authRepository: AuthRepository

    private lateinit var loginViewModel: LoginViewModel

    private val dummyLoginResponse = DataDummy.generateDummyLoginResponse()
    private val dummyToken = DataDummy.DUMMY_TOKEN
    private val dummyEmail = DataDummy.DUMMY_EMAIL
    private val dummyPassword = DataDummy.DUMMY_PASSWORD

    @Before
    fun setup() { loginViewModel = LoginViewModel(authRepository) }

    @Test
    fun `Login failed`() = runTest {
        val expectedResponse = MutableLiveData<Result<LoginResponse>>()
        expectedResponse.value = Result.failure(Exception("failed"))
        `when`(authRepository.userLogin(dummyEmail, dummyPassword)).thenReturn(expectedResponse)

        val actualResponse = loginViewModel.userLogin(dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(authRepository).userLogin(dummyEmail, dummyPassword)

        Assert.assertTrue(actualResponse.isFailure)
        Assert.assertFalse(actualResponse.isSuccess)
        actualResponse.onFailure { Assert.assertNotNull(it) }
    }

    @Test
    fun `Login success`() = runTest {
        val expectedResponse = MutableLiveData<Result<LoginResponse>>()
        expectedResponse.value = Result.success(dummyLoginResponse)
        `when`(authRepository.userLogin(dummyEmail, dummyPassword)).thenReturn(expectedResponse)

        val actualResponse = loginViewModel.userLogin(dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(authRepository).userLogin(dummyEmail, dummyPassword)

        Assert.assertTrue(actualResponse.isSuccess)
        Assert.assertFalse(actualResponse.isFailure)
        actualResponse.onSuccess {
            Assert.assertNotNull(it)
            Assert.assertSame(dummyLoginResponse, it)
        }
    }

    @Test
    fun `Store auth token success`() = runTest {
        loginViewModel.storeAuthToken(dummyToken)
        Mockito.verify(authRepository).storeAuthToken(dummyToken)
    }
}