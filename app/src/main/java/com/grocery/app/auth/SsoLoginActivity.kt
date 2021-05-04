package com.grocery.app.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.grocery.SharedPreferenceForLogin
import com.grocery.app.HomePage.HomePageActivity
import com.grocery.app.R
import com.grocery.app.activities.UpdateProfileActivity
import com.grocery.app.constant.USER
import com.grocery.app.extensions.showToast
import com.grocery.app.models.User
import com.grocery.app.utils.PrefManager
import java.net.URISyntaxException

class SsoLoginActivity : AppCompatActivity(), View.OnClickListener {


    private val prefManager: PrefManager by lazy { PrefManager.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sso_login_and_otp)
        listener()
//        if (FirebaseAuth.getInstance().currentUser != null) {
//            checkIfDetailsAreEntered()
//            finish()
//        }
    }

    private fun doFragmentTransaction(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.otpScreen, fragment)
        transaction.commit()
    }

    fun listener() {
        val closeBtn = findViewById<ImageView>(R.id.close)
        closeBtn.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        setListener()
    }

    private fun validatePhone(phoneNoET: EditText, phoneNo: String): Boolean {
        return when {
            phoneNo.isBlank() -> {
                phoneNoET.error = "Please enter your phone number"
                showToast("Please enter your phone number")
                false
            }
            phoneNo.length < 10 -> {
                phoneNoET.error = "Please enter a 10 digit phone \n number"
                false
            }
            else -> {
                true
            }
        }
    }

    private fun setListener() {
        val intent = Intent(this, OtpScreenActivity::class.java)
        val phoneNoET = findViewById<EditText>(R.id.phone_no_et)
        phoneNoET.requestFocus()
        val phoneNo = phoneNoET.text.toString()
        if (validatePhone(phoneNoET, phoneNo)) {
            intent.putExtra("Phone", "+91 $phoneNo")
            startActivity(intent)
        }
    }

}