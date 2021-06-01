package com.grocery.app.activities

import android.app.Activity
import android.content.Intent
import com.grocery.app.extensions.showToast
import com.grocery.app.listeners.ImagePickerListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView


abstract class ImagePickerActivity : BaseActivity(), ImagePickerListener {

    private var lastRequestCode = 3001

    fun startPickerActivity(requestCode: Int) {
        lastRequestCode=requestCode
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setOutputCompressQuality(45)
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                onImagePicked(lastRequestCode,result.uri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                showToast("Error occured while cropping image")
            }
        }
    }

}