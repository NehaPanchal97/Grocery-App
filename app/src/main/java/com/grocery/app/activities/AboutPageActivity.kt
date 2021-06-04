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
import com.grocery.app.extras.Result
import com.grocery.app.viewModels.AboutPageViewModel

class AboutPageActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binder: ActivityAboutPageBinding
    lateinit var viewModel: AboutPageViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_about_page)
        viewModel = ViewModelProvider(this).get(AboutPageViewModel::class.java)
        observe()
        onCallBtnPressed()
        onMessageBtnPressed()
        loadLocation()
        setUpView()
        viewModel.fetchStoreInfo()
        binder.ivBackArrow.setOnClickListener {
            onBackPressed()
        }
    }

    private fun onCallBtnPressed() {
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
                    binder.error = null
                }
                Result.Status.SUCCESS -> {
                    binder.store =
                        viewModel.store  //setting store can also be done through (it.data)
                    binder.loading = false
                }
                Result.Status.ERROR -> {
                    binder.loading = false
                    binder.error = "Error occurred!"
                }
            }
            binder.executePendingBindings()
        })
    }

    private fun setUpView() {
        binder.clickListener = this
        binder.executePendingBindings()
    }

    override fun onClick(v: View?) {
        viewModel.fetchStoreInfo()
    }
}

