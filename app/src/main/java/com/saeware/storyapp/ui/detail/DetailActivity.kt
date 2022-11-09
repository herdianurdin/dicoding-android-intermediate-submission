package com.saeware.storyapp.ui.detail

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.saeware.storyapp.R
import com.saeware.storyapp.data.remote.response.Story
import com.saeware.storyapp.databinding.ActivityDetailBinding
import com.saeware.storyapp.utils.ViewExtensions.setLocalDateFormat
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback (true) {
            override fun handleOnBackPressed() { finish() }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = intent.serializable<Story>(EXTRA_DETAIL)!!

        supportActionBar?.title = getString(R.string.story, story.name)

        postponeEnterTransition()
        parseStoryDate(story)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun parseStoryDate(story: Story) {
        binding.apply {
            tvName.text = story.name
            tvDate.setLocalDateFormat(story.createdAt)
            tvDescription.text = story.description

            Glide
                .with(this@DetailActivity)
                .load(story.photoUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        supportStartPostponedEnterTransition()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        supportStartPostponedEnterTransition()
                        return false
                    }
                })
                .placeholder(R.drawable.no_image)
                .error(R.drawable.image_error)
                .into(ivPhoto)
        }
    }

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
    }
}

inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}