package com.saeware.storyapp.utils

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.saeware.storyapp.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object ViewExtensions {
    fun ImageView.setImageFromUrl(context: Context, url: String) {
        Glide
            .with(context)
            .load(url)
            .placeholder(R.drawable.no_image)
            .error(R.drawable.image_error)
            .into(this)
    }

    fun TextView.setLocalDateFormat(timestamp: String) {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val date = simpleDateFormat.parse(timestamp) as Date
        val formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(date)
        this.text = formattedDate
    }

    fun View.setVisible(visible: Boolean) {
        this.visibility = if (visible) View.VISIBLE else View.GONE
    }
}