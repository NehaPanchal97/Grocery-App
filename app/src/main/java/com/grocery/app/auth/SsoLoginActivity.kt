package com.grocery.app.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.grocery.app.HomePage.HomePageActivity
import com.grocery.app.R
import kotlinx.android.synthetic.main.activity_account_details.*
import kotlinx.android.synthetic.main.activity_account_details.phone_no
import kotlinx.android.synthetic.main.activity_sso_login_and_otp.*

class SsoLoginActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sso_login_and_otp)
        listener()
        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(this,HomePageActivity::class.java))
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

    fun setListener(){
        var intent = Intent(this, OtpScreenActivity::class.java)
        var phoneNoText = findViewById<TextInputLayout>(R.id.phone_no_tv)
        var phoneNoET = findViewById<TextInputEditText>(R.id.phone_no_et)
        var phoneNo = phoneNoET.text.toString()
        if (phoneNo.isNullOrBlank()) {
            phoneNoText.error= "Please enter your phone number"
//            Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show()
        } else {
            intent.putExtra("Phone", "+91"+phoneNo)
            startActivity(intent)
        }
    }

}