package com.grocery.app.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.grocery.app.activities.AddCategoryActivity
import com.grocery.app.constant.CATEGORY
import com.grocery.app.models.Category
import java.util.*

class UpdateCategoryContract : ActivityResultContract<Category, Boolean>() {
    override fun createIntent(context: Context, input: Category?): Intent {
        return Intent(context, AddCategoryActivity::class.java).apply {
            putExtra(CATEGORY, input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}