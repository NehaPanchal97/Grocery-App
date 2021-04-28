package com.grocery.app.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.grocery.app.HomePage.HomePageActivity
import com.grocery.app.R
import com.grocery.app.extensions.showToast

class SsoLoginActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sso_login_and_otp)
        listener()
        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(this, HomePageActivity::class.java))
            finish()
        }
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

    fun validatePhone(phoneNoET: EditText, phoneNo: String): Boolean{
        if (phoneNo.isNullOrBlank()) {
            phoneNoET.error = "Please enter your phone number"
//           Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show()
            showToast("Please enter your phone number")
            return false
        } else if (phoneNo.length < 10) {
            phoneNoET.error = "Please enter a 10 digit phone \n number"
            return false
        }
        else{
            return true
        }
    }
    fun setListener() {
        var intent = Intent(this, OtpScreenActivity::class.java)
        var phoneNoET = findViewById<EditText>(R.id.phone_no_et)
        phoneNoET.requestFocus()
        var phoneNo = phoneNoET.text.toString()
        if (validatePhone(phoneNoET,phoneNo)){
            intent.putExtra("Phone", "+91" + phoneNo)
            startActivity(intent)
        }
    }

}