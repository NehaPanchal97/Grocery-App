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
        setContentView(R.layout.activity_sso_login)
    }


    override fun onClick(v: View?) {
        setListener()
    }

    private fun validatePhone(phoneNoET: EditText, phoneNo: String): Boolean {
        return when {
            phoneNo.isBlank() -> {
                phoneNoET.error = getString(R.string.entering_mobile_number)
                false
            }
            phoneNo.length < 10 -> {
                phoneNoET.error = getString(R.string.enter_10_digit_mobile_number)
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