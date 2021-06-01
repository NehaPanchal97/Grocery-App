package com.grocery.app.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.grocery.app.R
import com.grocery.app.databinding.ActivityAboutPageBinding

class AboutPageActivity : AppCompatActivity() {

    lateinit var binder: ActivityAboutPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_about_page)
        onCallPressed()
        loadLocation()
    }

    private fun onCallPressed() {
        binder.ivCallBtn.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:+919876543210")
            startActivity(callIntent)
        }
    }

    private fun loadLocation() {
        binder.ivLocation.setOnClickListener {
            val uri =
                "http://maps.google.com/maps?q=Nature Basket, Century Bazaar, Prabhadevi, Mumbai, Maharashtra"
            val intent: Intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(uri)
            )
            intent .setPackage("com.google.android.apps.maps")
            intent.resolveActivity(packageManager)?.let {
                startActivity (intent)
            }
        }
    }
}

