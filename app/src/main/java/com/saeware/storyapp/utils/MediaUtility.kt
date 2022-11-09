package com.saeware.storyapp.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

object MediaUtility {
    private const val FILENAME_FORMAT = "dd-MMM-yyyy"

    private val timestamp: String =
        SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())

    fun createTempFile(context: Context): File {
        val directory: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timestamp, ".jpg", directory)
    }

    fun reduceImageSize(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int

        do {
            val bitmapStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bitmapStream)
            val bitmapByteArray = bitmapStream.toByteArray()
            streamLength = bitmapByteArray.size
            compressQuality -= 5
        } while (streamLength > 1_000_000)

        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

        return file
    }

    fun uriToFile(imageUri: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val file = createTempFile(context)
        val inputStream = contentResolver.openInputStream(imageUri) as InputStream
        val outputStream: OutputStream = FileOutputStream(file)
        val buffer = ByteArray(1024)

        var length: Int

        while (inputStream.read(buffer).also { length = it } > 0 )
            outputStream.write(buffer, 0, length)

        outputStream.close()
        inputStream.close()

        return file
    }
}