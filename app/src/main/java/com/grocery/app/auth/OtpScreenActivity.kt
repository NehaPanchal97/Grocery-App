package com.grocery.app.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.grocery.app.HomePage.HomeFragment
import com.grocery.app.R
import com.mukesh.OtpView
import kotlinx.android.synthetic.main.activity_otp_screen.*
import java.util.concurrent.TimeUnit

class OtpScreenActivity : AppCompatActivity(), View.OnClickListener {
    private var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@OtpScreenActivity, "$e", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                Log.d("code", "onCodeSent: $verificationId")
                mVerificationId = verificationId
                forceResendingToken = token
                progressBar.visibility = View.GONE


                Toast.makeText(
                    this@OtpScreenActivity,
                    "Verification code sent...",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }


    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null
    private var mVerificationId: String? = null
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_screen)

        firebaseAuth = FirebaseAuth.getInstance()
        listener()
        setListener()
        val otpView = findViewById<OtpView>(R.id.otp_view)
        otpView.itemCount = 6

        var mobile = intent.getStringExtra("Phone")
        var tvNumber = findViewById<TextView>(R.id.tv_phone_no)
        tvNumber.setText(String.format(getString(R.string.code_sent), mobile))
        if (mobile != null) {
            startPhoneNumberVerification(mobile)
        }


        val resendCode = findViewById<TextView>(R.id.tv_resend_code)
        resendCode.setOnClickListener {
            if (mobile != null) {
                forceResendingToken?.let { it1 -> resendVerificationCode(mobile, it1) }
            }
        }
    }

    private fun startPhoneNumberVerification(mobile: String) {
        progressBar.visibility = View.VISIBLE
        val authentication = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(mobile)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(authentication)
    }

    private fun resendVerificationCode(
        mobile: String,
        token: PhoneAuthProvider.ForceResendingToken
    ) {
        progressBar.visibility = View.VISIBLE
        val authentication = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(mobile)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallbacks)
            .setForceResendingToken(token)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(authentication)
    }

    private fun verifyPhoneNumberWithCode(verificationId: String, code: String) {
        progressBar.visibility = View.VISIBLE
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        progressBar.visibility = View.VISIBLE

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    progressBar.visibility = View.GONE
                    val phone = firebaseAuth.currentUser.phoneNumber
                    Toast.makeText(this, "Logged in as $phone", Toast.LENGTH_SHORT).show()
                    otpContainer.visibility = View.GONE
                    doFragmentTransaction(AuthenticationFragment())
//                    startActivity(Intent(this, AccountDetailsActivity::class.java))
//                    finish()

                } else {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "${task.exception}", Toast.LENGTH_SHORT).show()
                }

            }
    }

    override fun onClick(v: View?) {
        val otpView = findViewById<OtpView>(R.id.otp_view)
        val otp = otpView.text.toString()
        if (TextUtils.isEmpty(otp)) {
            otpView.error = "Please enter the verification code"
            otpView.requestFocus()
//            Toast.makeText(this, "Please enter the verification code", Toast.LENGTH_SHORT).show()
        } else {
            mVerificationId?.let { verifyPhoneNumberWithCode(it, otp) }
        }
    }

    private fun listener() {
        val otpView = findViewById<OtpView>(R.id.otp_view)
        otpView.setOtpCompletionListener { otp ->
            Log.d("otp", "otp $otp")
        }
    }

    private fun setListener() {
        var backArrow = findViewById<ImageView>(R.id.arrow_back)
        backArrow.setOnClickListener {
            onBackPressed()
        }
    }

    fun doFragmentTransaction(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.otpScreen, fragment)
        transaction.commit()
    }

}

