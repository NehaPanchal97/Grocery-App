package com.grocery.app.activities

import android.app.Activity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.grocery.app.R
import com.grocery.app.auth.UpdateProfileFragment
import com.grocery.app.databinding.ActivityUpdateProfileBinding
import com.grocery.app.listeners.OnProfileUpdatedListener

class UpdateProfileActivity : BaseActivity(), OnProfileUpdatedListener {

    private lateinit var binder: ActivityUpdateProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_update_profile)

        setupView()
        supportFragmentManager.beginTransaction()
            .replace(R.id.root_view, UpdateProfileFragment())
            .commit()
    }

    private fun setupView() {
        binder.arrowBack.setOnClickListener { onBackPressed() }
    }

    override fun onProfileUpdated() {
        setResult(Activity.RESULT_OK)
        finish()
    }
}