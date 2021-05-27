package com.grocery.app.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.grocery.app.R
import com.grocery.app.databinding.ActivityAboutPageBinding

class AboutPageActivity : AppCompatActivity() {

    lateinit var binder: ActivityAboutPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_about_page)
        onCallPressed()

    }

    private fun onCallPressed() {
        binder.ivCallBtn.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:+919987384423")
            startActivity(callIntent)
        }
    }
}

