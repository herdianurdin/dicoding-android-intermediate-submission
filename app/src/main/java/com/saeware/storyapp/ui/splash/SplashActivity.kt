package com.saeware.storyapp.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.saeware.storyapp.ui.auth.AuthActivity
import com.saeware.storyapp.ui.main.MainActivity
import com.saeware.storyapp.ui.main.MainActivity.Companion.EXTRA_TOKEN
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        determineScreenDirection()
    }

    private fun determineScreenDirection() {
        viewModel.getAuthToken().observe(this) { token ->
            if (token.isNullOrEmpty())
                Intent(this@SplashActivity, AuthActivity::class.java)
                    .also {
                        startActivity(it)
                        finish()
                    }
            else
                Intent(this@SplashActivity, MainActivity::class.java)
                    .also {
                        it.putExtra(EXTRA_TOKEN, token)
                        startActivity(it)
                        finish()
                    }
        }
    }
}