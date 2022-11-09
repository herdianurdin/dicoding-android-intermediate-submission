package com.saeware.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saeware.storyapp.R
import com.saeware.storyapp.adapter.StoryAdapter
import com.saeware.storyapp.data.remote.response.Story
import com.saeware.storyapp.databinding.ActivityMainBinding
import com.saeware.storyapp.ui.auth.AuthActivity
import com.saeware.storyapp.ui.detail.DetailActivity
import com.saeware.storyapp.ui.detail.DetailActivity.Companion.EXTRA_DETAIL
import com.saeware.storyapp.ui.post.PostActivity
import com.saeware.storyapp.utils.MessageUtility.showToast
import com.saeware.storyapp.utils.ViewExtensions.setVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var storyAdapter: StoryAdapter
    private lateinit var recyclerView: RecyclerView

    private val viewModel: MainViewModel by viewModels()

    private var token: String = ""

    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialog.Builder(this@MainActivity).apply {
                    setTitle(getString(R.string.exit))
                    setMessage(getString(R.string.confirmation_message))
                    setPositiveButton(getString(R.string.yes)) { _, _ -> finish()}
                    setNegativeButton(getString(R.string.no)) { _, _ -> }
                    create()
                    show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = intent.getStringExtra(EXTRA_TOKEN)!!

        setRecyclerView()
        getStories()
        swipeRefresh()

        binding.fabPost.setOnClickListener {
            Intent(this@MainActivity, PostActivity::class.java)
                .also {
                    it.putExtra(EXTRA_TOKEN, token)
                    startActivity(it)
                }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> handleActionSettings()
            R.id.action_logout -> handleActionLogout()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun handleActionSettings() { startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS)) }

    private fun handleActionLogout() {
        AlertDialog.Builder(this@MainActivity).apply {
            setTitle(R.string.logout)
            setMessage(getString(R.string.confirmation_message))
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.storeAuthToken("")
                Intent(this@MainActivity, AuthActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
            setNegativeButton(getString(R.string.no)) { _, _ -> }
            create()
            show()
        }
    }

    private fun getStories(isRefresh: Boolean = false) {
        showLoading(isRefresh, true)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.getAllStories(token).observe(this@MainActivity) { result ->
                    result.onSuccess {
                        storyAdapter.submitList(it.listStory)

                        showLoading(isRefresh, false)
                        showErrorOccurred(false)
                    }
                    result.onFailure {
                        showLoading(isRefresh, false)
                        showErrorOccurred(true)
                    }
                }
            }
        }
    }

    private fun setRecyclerView() {
        storyAdapter = StoryAdapter()
        recyclerView = binding.rvStories

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storyAdapter
        }

        storyAdapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(story: Story, bundle: Bundle?) {
                Intent(this@MainActivity, DetailActivity::class.java).also {
                    it.putExtra(EXTRA_DETAIL, story)
                    startActivity(it, bundle)
                }
            }
        })
    }

    private fun swipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener { getStories(true) }
    }

    private fun showLoading(isRefresh: Boolean, isLoading: Boolean) {
        if (isRefresh) binding.swipeRefresh.isRefreshing = isLoading
        else binding.progressBar.setVisible(isLoading)
    }

    private fun showErrorOccurred(isError: Boolean) {
        binding.apply {
            tvErrorResult.setVisible(isError)
            rvStories.setVisible(!isError)
        }

        if (isError) showToast(this@MainActivity, getString(R.string.error_occurred))
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }
}