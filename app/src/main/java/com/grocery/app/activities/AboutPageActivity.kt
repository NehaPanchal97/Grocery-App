package com.grocery.app.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.grocery.app.R
import com.grocery.app.databinding.ActivityAboutPageBinding
import com.grocery.app.extensions.showError
import com.grocery.app.extras.Result
import com.grocery.app.viewModels.AboutPageViewModel

class AboutPageActivity : AppCompatActivity() {

    lateinit var binder: ActivityAboutPageBinding
    lateinit var viewModel : AboutPageViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_about_page)
        viewModel = ViewModelProvider(this).get(AboutPageViewModel::class.java)
        onCallPressed()
        loadLocation()
        observe()
        setUpView()
        viewModel.fetchStoreInfo()
    }

    private fun onCallPressed() {
        binder.ivCallBtn.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:"+viewModel.contact)
            startActivity(callIntent)
        }
    }

    private fun loadLocation() {
        binder.ivLocation.setOnClickListener {
//            val uri =
                "http://maps.google.com/maps?q=Nature Basket, Century Bazaar, Prabhadevi, Mumbai, Maharashtra"
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(viewModel.uri)
            )
            intent .setPackage("com.google.android.apps.maps")
            intent.resolveActivity(packageManager)?.let {
                startActivity (intent)
            }
        }
    }

    private fun observe(){
        viewModel.fetchStoreLiveData.observe(this, Observer {
            when(it.type){
                Result.Status.LOADING ->{
                }
                Result.Status.SUCCESS ->{
                    it.data
                }
                Result.Status.ERROR -> {
                    binder.root.showError("Cannot fetch data")
                }
            }
        })
    }

    private fun setUpView(){
        binder.tvStoreName.text = viewModel.storeName

    }
}

