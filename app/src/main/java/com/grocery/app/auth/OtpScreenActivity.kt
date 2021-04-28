package com.grocery.app.auth

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.grocery.app.R
import com.grocery.app.databinding.ActivityOtpScreenBinding
import com.grocery.app.extensions.authUser
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

    private lateinit var binder: ActivityOtpScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_otp_screen)

        listener()
        binder.arrowBack.setOnClickListener { onBackPressed() }
        val otpView = findViewById<OtpView>(R.id.otp_view)
        otpView.itemCount = 6

        val mobile = intent.getStringExtra("Phone")
        val tvNumber = findViewById<TextView>(R.id.tv_phone_no)
        tvNumber.text = String.format(getString(R.string.code_sent), mobile)
        mobile?.let { startPhoneNumberVerification(it) }


        val resendCode = findViewById<TextView>(R.id.tv_resend_code)
        resendCode.setOnClickListener {
            if (mobile != null) {
                forceResendingToken?.let { it1 -> resendVerificationCode(mobile, it1) }
            }
        }
    }

    private fun startPhoneNumberVerification(mobile: String) {
        progressBar.visibility = View.VISIBLE
        val authentication = PhoneAuthOptions.newBuilder(Firebase.auth)
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
        val authentication = PhoneAuthOptions.newBuilder(Firebase.auth)
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

        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    progressBar.visibility = View.GONE
                    val phone =  authUser?.phoneNumber
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

    private fun doFragmentTransaction(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.otpScreen, fragment)
        transaction.commit()
    }

}

