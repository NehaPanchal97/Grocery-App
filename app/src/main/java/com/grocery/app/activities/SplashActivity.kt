package com.grocery.app.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.grocery.app.HomePage.HomePageActivity
import com.grocery.app.R
import com.grocery.app.auth.SsoLoginActivity
import com.grocery.app.constant.USER
import com.grocery.app.models.User
import com.grocery.app.utils.PrefManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity() {

    private val prefManager by lazy { PrefManager.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        decideNavigation()
    }

    private fun decideNavigation() = GlobalScope.launch(Dispatchers.Main) {
        delay(300)
        val auth = Firebase.auth.currentUser
        val aClass = auth?.let {
            val user = prefManager.get<User>(USER)
            if (user == null || user.name.isNullOrEmpty()) {
                UpdateProfileActivity::class.java
            } else {
                //Land to Home Page
                HomePageActivity::class.java
            }
        } ?: kotlin.run {
            SsoLoginActivity::class.java
        }
        startActivity(Intent(this@SplashActivity, aClass))
        finish()
    }
}