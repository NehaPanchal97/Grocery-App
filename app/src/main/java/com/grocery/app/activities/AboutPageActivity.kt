package com.grocery.app.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
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
            callIntent.data = Uri.parse("tel:+919987384423")
            startActivity(callIntent)
        }
    }

    private fun loadLocation() {
        binder.ivLocation.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:37.7749,-122.4194")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            mapIntent.resolveActivity(packageManager)?.let {
                startActivity(mapIntent)
            }
        }
    }

    fun onMapReady(googleMap: GoogleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        val sydney = LatLng(37.7749, -122.4194)
        googleMap.addMarker(
            MarkerOptions()
                .title("Grocery shop")
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}

