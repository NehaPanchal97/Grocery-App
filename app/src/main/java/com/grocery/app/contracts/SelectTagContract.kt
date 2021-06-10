package com.grocery.app.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.grocery.app.activities.SelectTagActivity
import com.grocery.app.constant.TAGS

class SelectTagContract :
    ActivityResultContract<ArrayList<String?>?, ArrayList<String?>?>() {
    override fun createIntent(context: Context, input: ArrayList<String?>?): Intent {
        return Intent(context, SelectTagActivity::class.java).apply {
            putStringArrayListExtra(TAGS, input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): ArrayList<String?>? {
        if (resultCode == Activity.RESULT_OK) {
            return intent?.getStringArrayListExtra(TAGS)
        }
        return null
    }
}