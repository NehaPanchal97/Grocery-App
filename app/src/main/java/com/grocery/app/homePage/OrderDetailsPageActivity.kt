package com.grocery.app.homePage

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.grocery.app.R
import com.grocery.app.databinding.OrderDetailsPageBinding

class OrderDetailsPageActivity : AppCompatActivity() {


    lateinit var  binder:OrderDetailsPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binder = DataBindingUtil.setContentView(this,R.layout.order_details_page)
        listener()
    }


    private fun listener() {
        val closeBtn =binder.orderBackBtn
        closeBtn.setOnClickListener {
            onBackPressed()
        }
    }
}