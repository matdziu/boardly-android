package com.boardly.extensions

import android.content.Context
import android.support.annotation.DrawableRes
import android.widget.ImageView
import com.boardly.injection.modules.GlideApp
import java.io.File

fun Context.loadImageFromUrl(imageView: ImageView, pictureUrl: String, @DrawableRes placeholderId: Int) {
    GlideApp.with(this)
            .load(pictureUrl)
            .placeholder(placeholderId)
            .into(imageView)
}

fun Context.loadImageFromFile(imageView: ImageView, pictureFile: File) {
    GlideApp.with(this)
            .load(pictureFile)
            .into(imageView)
}