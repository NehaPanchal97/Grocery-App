package com.grocery.app.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.grocery.app.activities.AddProductActivity
import com.grocery.app.constant.PRODUCT
import com.grocery.app.models.Product

class UpdateProductContract : ActivityResultContract<Product?, Boolean>() {

    override fun createIntent(context: Context, input: Product?): Intent {
        return Intent(context, AddProductActivity::class.java).apply {
            putExtra(PRODUCT, input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return (resultCode == Activity.RESULT_OK)
    }
}