package com.saeware.storyapp.ui.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.saeware.storyapp.data.AuthRepository
import com.saeware.storyapp.data.remote.response.RegisterResponse
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
class RegisterViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock private lateinit var authRepository: AuthRepository

    private lateinit var registerViewModel: RegisterViewModel

    private val dummyRegisterResponse = DataDummy.generateDummyRegisterResponse()
    private val dummyName = DataDummy.DUMMY_NAME
    private val dummyEmail = DataDummy.DUMMY_EMAIL
    private val dummyPassword = DataDummy.DUMMY_PASSWORD

    @Before
    fun setup() { registerViewModel = RegisterViewModel(authRepository) }

    @Test
    fun `Register failed`() = runTest {
        val expectedResponse = MutableLiveData<Result<RegisterResponse>>()
        expectedResponse.value = Result.failure(Exception("failed"))
        `when`(authRepository.userRegister(dummyName, dummyEmail, dummyPassword))
            .thenReturn(expectedResponse)

        val actualResponse = registerViewModel.userRegister(dummyName, dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(authRepository).userRegister(dummyName, dummyEmail, dummyPassword)

        Assert.assertTrue(actualResponse.isFailure)
        Assert.assertFalse(actualResponse.isSuccess)
        actualResponse.onFailure { Assert.assertNotNull(it) }
    }

    @Test
    fun `Register success`() = runTest {
        val expectedResponse = MutableLiveData<Result<RegisterResponse>>()
        expectedResponse.value = Result.success(dummyRegisterResponse)
        `when`(authRepository.userRegister(dummyName, dummyEmail, dummyPassword))
            .thenReturn(expectedResponse)

        val actualResponse = registerViewModel.userRegister(dummyName, dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(authRepository).userRegister(dummyName, dummyEmail, dummyPassword)

        Assert.assertTrue(actualResponse.isSuccess)
        Assert.assertFalse(actualResponse.isFailure)
        actualResponse.onSuccess {
            Assert.assertNotNull(it)
            Assert.assertSame(dummyRegisterResponse, it)
        }
    }
}