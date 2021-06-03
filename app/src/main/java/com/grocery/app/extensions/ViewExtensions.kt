package com.grocery.app.extensions

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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

fun Context.showToast(message: String?, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message ?: "", duration).show()
}

//Load image
fun ImageView.loadImage(
    url: String?,
    placeholder: Int = R.drawable.ic_shop,
    circular: Boolean = false,
    cache: Boolean = true
) {
    val builder = Glide.with(this)
        .load(url)
        .placeholder(placeholder)
        .diskCacheStrategy(if (cache) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE)
        .skipMemoryCache(!cache)
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

fun View.inVisible(show: Boolean = true) {
    this.visibility = if (show) View.VISIBLE else View.INVISIBLE
}

fun View.disable(duration: Long = 200) = GlobalScope.launch(Dispatchers.Main) {
    this@disable.isEnabled = false
    delay(duration)
    this@disable.isEnabled = true
}

fun Activity.hideKeyboard() {
    val imm =
        this.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view: View? = this.currentFocus
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}