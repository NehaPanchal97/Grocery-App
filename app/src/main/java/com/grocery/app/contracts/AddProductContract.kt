package com.grocery.app.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.grocery.app.activities.AddProductActivity

class AddProductContract : ActivityResultContract<Void?, Boolean>() {

    override fun createIntent(context: Context, input: Void?): Intent {
        return Intent(context, AddProductActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}