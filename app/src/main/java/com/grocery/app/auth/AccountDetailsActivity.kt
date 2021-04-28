package com.grocery.app.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.grocery.app.R

class AccountDetailsActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_details)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            Firebase.auth.signOut()
            Toast.makeText(this, "Logging out..", Toast.LENGTH_SHORT).show()
            checkUser()
        }
    }

    private fun checkUser() {
        val user = firebaseAuth.currentUser
        if (user == null){
            //logged out
            startActivity(Intent(this,SsoLoginActivity::class.java))
            finish()
        }
        else{
            val phone = user.phoneNumber
            val phoneNo_tv = findViewById<TextInputLayout>(R.id.tv_mobile)
            phoneNo_tv.editText?.setText(phone)
        }
    }
}