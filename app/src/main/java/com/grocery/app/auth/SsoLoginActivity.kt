package com.grocery.app.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.grocery.app.R
import com.grocery.app.extensions.showToast

class SsoLoginActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sso_login_and_otp)
        listener()
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