package com.grocery.app.extensions

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.MenuRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.grocery.app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun View.showError(message: String?, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, message ?: "", duration)
        .setAction("OK", {})
        .setBackgroundTint(Color.RED)
        .show()
}

fun View.showSuccess(message: String?, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, message ?: "", duration)
        .setAction("OK", {})
        .setBackgroundTint(Color.BLACK)
        .show()
}

fun Context.showToast(message: String?) {
    Toast.makeText(this, message ?: "", Toast.LENGTH_SHORT).show()
}

//Load image
fun ImageView.loadImage(
    url: String?,
    placeholder: Int = R.drawable.ic_shop,
    circular: Boolean = false
) {
    val builder = Glide.with(this)
        .load(url)
        .placeholder(placeholder)
    if (circular)
        builder.transform(CircleCrop())
    builder.into(this)
}

fun View.show() {
    visible(true)
}

fun View.hide() {
    visible(false)
}

fun View.visible(show: Boolean = true) {
    this.visibility = if (show) View.VISIBLE else View.GONE
}

fun View.disable(duration: Long = 200) = GlobalScope.launch(Dispatchers.Main) {
    this@disable.isEnabled = false
    delay(duration)
    this@disable.isEnabled = true
}