package com.grocery.app.activities

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.grocery.app.R
import com.grocery.app.auth.UpdateProfileFragment
import com.grocery.app.databinding.ActivityUpdateProfileBinding

class UpdateProfileActivity : BaseActivity() {

    private lateinit var binder: ActivityUpdateProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_update_profile)

        supportFragmentManager.beginTransaction()
            .replace(R.id.root_view, UpdateProfileFragment())
            .commit()
    }
}