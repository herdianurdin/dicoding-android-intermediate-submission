package com.saeware.storyapp.ui.settings

import com.saeware.storyapp.data.AuthRepository
import com.saeware.storyapp.utils.CoroutineTestUtil
import com.saeware.storyapp.utils.DataDummy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class SettingsViewModelTest {
    @get:Rule
    val coroutineTestUtil = CoroutineTestUtil()

    @Mock private lateinit var authRepository: AuthRepository

    private lateinit var settingsViewModel: SettingsViewModel

    private val dummyToken = DataDummy.DUMMY_TOKEN

    @Before
    fun setup() { settingsViewModel = SettingsViewModel(authRepository) }

    @Test
    fun `Store auth token success`() = runTest {
        settingsViewModel.storeAuthToken(dummyToken)
        Mockito.verify(authRepository).storeAuthToken(dummyToken)
    }
}