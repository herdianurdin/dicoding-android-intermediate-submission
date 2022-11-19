package com.saeware.storyapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    fun getAuthToken(): LiveData<String?> = dataStore.data.map { it[TOKEN_KEY] }.asLiveData()

    suspend fun storeAuthToken(token: String) { dataStore.edit { it[TOKEN_KEY] = token } }

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("user_token")
    }
}