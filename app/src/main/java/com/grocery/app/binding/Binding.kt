package com.grocery.app.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.grocery.app.R
import com.grocery.app.extensions.loadImage


@BindingAdapter("loadUrl", "circular", "placeholder")
fun ImageView.loadUrl(
    url: String?,
    circular: Boolean = false,
    placeholder: Int = R.drawable.ic_shop
) {
    this.loadImage(url, circular = circular, placeholder = placeholder)
}