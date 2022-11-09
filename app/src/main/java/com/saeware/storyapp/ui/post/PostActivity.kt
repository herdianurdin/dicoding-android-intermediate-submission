package com.saeware.storyapp.ui.post

import android.animation.AnimatorSet
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import com.saeware.storyapp.R
import com.saeware.storyapp.databinding.ActivityPostBinding
import com.saeware.storyapp.ui.main.MainActivity.Companion.EXTRA_TOKEN
import com.saeware.storyapp.utils.AnimationUtility.setFadeViewAnimation
import com.saeware.storyapp.utils.MediaUtility
import com.saeware.storyapp.utils.MediaUtility.reduceImageSize
import com.saeware.storyapp.utils.MediaUtility.uriToFile
import com.saeware.storyapp.utils.MessageUtility.showToast
import com.saeware.storyapp.utils.ViewExtensions.setVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding
    private lateinit var currentImagePath: String

    private var getFile: File? = null
    private var token: String = ""

    private val viewModel: PostViewModel by viewModels()

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val file = File(currentImagePath).also { getFile = it }

            val bitmap = BitmapFactory.decodeFile(getFile?.path)
            val exifInterface = ExifInterface(currentImagePath)
            val orientation: Int = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
            val rotatedBitmap: Bitmap = when(orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
                else -> bitmap
            }

            try {
                flowOf(1)
                    .onEach {
                        val outputStream = FileOutputStream(file)

                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        outputStream.flush()
                        outputStream.close()

                        getFile = file
                    }.flowOn(Dispatchers.IO)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            binding.ivPreviewUpload.setImageBitmap(rotatedBitmap)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImage: Uri = result.data?.data as Uri
            uriToFile(selectedImage, this@PostActivity).also { getFile = it }

            binding.ivPreviewUpload.setImageURI(selectedImage)
        }
    }

    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { finish() }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.post_new_story_title)

        playAnimation()
        init()

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun init() {
        token = intent.getStringExtra(EXTRA_TOKEN)!!

        binding.apply {
            btnImageFromCamera.setOnClickListener { startIntentCamera() }
            btnImageFromGallery.setOnClickListener { startIntentGallery() }
            btnUploadPost.setOnClickListener { uploadStory() }
        }
    }

    private fun uploadStory() {
        showLoading(true)

        val edtDescription = binding.edtDescription
        
        when {
            getFile == null -> showInputError(getString(R.string.empty_field_image))
            edtDescription.text.toString().isBlank() -> {
                val message = getString(R.string.empty_field, getString(R.string.description))
                showInputError(message)
                binding.edtDescription.error = message
            }
            else -> {
                lifecycleScope.launchWhenStarted {
                    val description =
                        edtDescription.text.toString().toRequestBody("text/plain".toMediaType())
                    val file = reduceImageSize(getFile as File)
                    val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "photo",
                        file.name,
                        requestImageFile
                    )

                    viewModel.postStory(
                        token, imageMultipart, description
                    ).observe(this@PostActivity) { result ->
                        result.onSuccess {
                            showToast(this@PostActivity, getString(R.string.upload_success))
                            finish()
                        }
                        result.onFailure {
                            showToast(this@PostActivity, getString(R.string.upload_failed))
                            showLoading(false)
                        }
                    }
                }
            }
        }
    }

    private fun startIntentGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"

        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private fun startIntentCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        MediaUtility.createTempFile(application).also {
            val imageUri = FileProvider.getUriForFile(
                this@PostActivity,
                "com.saeware.storyapp",
                it
            )
            currentImagePath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            progressBar.setVisible(isLoading)
            btnImageFromCamera.isEnabled = !isLoading
            btnImageFromGallery.isEnabled = !isLoading
            btnUploadPost.isEnabled = !isLoading
        }
    }

    private fun showInputError(message: String) {
        showLoading(false)
        showToast(this@PostActivity, message)
    }

    private fun playAnimation() {
        val ivPreviewUpload = setFadeViewAnimation(binding.ivPreviewUpload)
        val tvLabelChooseImage = setFadeViewAnimation(binding.tvLabelChooseImage)
        val btnImageFromCamera = setFadeViewAnimation(binding.btnImageFromCamera)
        val btnImageFromGallery = setFadeViewAnimation(binding.btnImageFromGallery)
        val tvLabelWriteDescription = setFadeViewAnimation(binding.tvLabelWriteDescription)
        val edtDescriptionLayout = setFadeViewAnimation(binding.edtDescriptionLayout)
        val btnUploadPost = setFadeViewAnimation(binding.btnUploadPost)

        AnimatorSet().apply {
            playSequentially(
                ivPreviewUpload, tvLabelChooseImage, btnImageFromCamera, btnImageFromGallery,
                tvLabelWriteDescription, edtDescriptionLayout, btnUploadPost
            )
            startDelay = 300
        }.start()
    }
}