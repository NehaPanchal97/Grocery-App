
package com.grocery.app.homePage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R
import com.grocery.app.adapters.ProductListAdapter
import com.grocery.app.constant.CART
import com.grocery.app.constant.ORDER_DESCRIPTION_ITEM_TYPE
import com.grocery.app.databinding.OrderDetailsPageBinding
import com.grocery.app.models.Cart
import com.grocery.app.utils.PrefManager
import com.grocery.app.viewModels.OrderViewModel
import com.grocery.app.viewModels.ProductViewModel

class OrderDetailsPageActivity : AppCompatActivity() {


    lateinit var binder: OrderDetailsPageBinding
    private lateinit var listAdapter: ProductListAdapter
    lateinit var productViewModel: ProductViewModel
    lateinit var pref: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.order_details_page)
        binder.rvOrderDescription.layoutManager =
            LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        listener()
        setUpView()
    }


    private fun listener() {
        val closeBtn = binder.orderBackBtn
        closeBtn.setOnClickListener {
            onBackPressed()
        }
    }
    private fun setUpView() {
        pref = PrefManager.getInstance(applicationContext)
        initCart()
        binder.rvOrderDescription.apply {
            listAdapter = ProductListAdapter(
                productViewModel.cart.items ?: arrayListOf(),
                ORDER_DESCRIPTION_ITEM_TYPE
            )
            adapter = listAdapter
        }
    }

    private fun initCart() {
        productViewModel.cart = pref.get(CART) ?: Cart()
        productViewModel.initCart()
    }
}