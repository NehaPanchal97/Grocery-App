package com.grocery.app.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.grocery.app.activities.AddCategoryActivity

class AddCategoryContract : ActivityResultContract<Void, Boolean>() {
    override fun createIntent(context: Context, input: Void?): Intent {
        return Intent(context, AddCategoryActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}