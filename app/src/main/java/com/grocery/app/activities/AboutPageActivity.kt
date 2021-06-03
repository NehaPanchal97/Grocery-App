package com.grocery.app.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.grocery.app.R
import com.grocery.app.databinding.ActivityAboutPageBinding
import com.grocery.app.extensions.showError
import com.grocery.app.extensions.visible
import com.grocery.app.extras.Result
import com.grocery.app.viewModels.AboutPageViewModel

class AboutPageActivity : AppCompatActivity() {

    lateinit var binder: ActivityAboutPageBinding
    lateinit var viewModel: AboutPageViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_about_page)
        viewModel = ViewModelProvider(this).get(AboutPageViewModel::class.java)
        observe()
        onCallPressed()
        onMessageBtnPressed()
        loadLocation()
        viewModel.fetchStoreInfo()
    }

    private fun onCallPressed() {
        binder.ivCallBtn.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:" + viewModel.store?.contact)
            startActivity(callIntent)
        }
    }

    private fun onMessageBtnPressed() {
        binder.ivMessage.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_VIEW)
            sendIntent.data = Uri.parse("sms:" + viewModel.store?.contact)
            startActivity(sendIntent)
        }
    }

    private fun loadLocation() {
        binder.ivLocation.setOnClickListener {
//            val uri =
            "http://maps.google.com/maps?q=Nature Basket, Century Bazaar, Prabhadevi, Mumbai, Maharashtra"
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(viewModel.store?.locationUri)
            )
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)

        }
    }

    private fun observe() {
        viewModel.fetchStoreLiveData.observe(this, Observer {
            when (it.type) {
                Result.Status.LOADING -> {
                    binder.loading = true
                }
                Result.Status.SUCCESS -> {
                    binder.store = viewModel.store
                    binder.loading = false
//                    it.data
//                   setData()
                }
                Result.Status.ERROR -> {
                    binder.storeContainer.visible(false)
                    binder.detailsContainer.visible(false)
                    binder.loading = false
                    binder.blankScreenText.visible(true)
                }
            }
            binder.executePendingBindings()
        })
    }

    private fun setData() {
//        binder.tvStoreName.text = viewModel.store?.storeName
//        binder.tvStoreTime.text = viewModel.store?.timing
        binder.tvCallDescription.text =
            getString(R.string.callText, viewModel.store?.callDescription)
        binder.tvLocationDescription.text = viewModel.store?.locationDescription
    }
}

