package com.grocery.app.listeners

import android.net.Uri

interface ImagePickerListener {
    fun onImagePicked(requestCode: Int, uri: Uri)
}