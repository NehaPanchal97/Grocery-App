package com.grocery.app.homePage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R
import com.grocery.app.activities.DetailsPageActivity
import com.grocery.app.adapters.ProductListAdapter
import com.grocery.app.constant.*
import com.grocery.app.databinding.ActivityDetailsPageBinding
import com.grocery.app.databinding.OrderStatusPageBinding
import com.grocery.app.extensions.showError
import com.grocery.app.extensions.showSuccess
import com.grocery.app.extras.Result
import com.grocery.app.homePage.adapters.OrderStatusAdapter
import com.grocery.app.listeners.OnCategoryClickListener
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.models.Cart
import com.grocery.app.models.Category
import com.grocery.app.models.Order
import com.grocery.app.utils.PrefManager
import com.grocery.app.viewModels.OrderViewModel
import com.grocery.app.viewModels.ProductViewModel


class OrderStatusPageActivity : AppCompatActivity() {


    lateinit var  binder:OrderStatusPageBinding
    lateinit var viewModel:OrderViewModel
    private lateinit var listAdapter: ProductListAdapter
    private lateinit var orderAdapter:OrderStatusAdapter
    lateinit var productViewModel: ProductViewModel
    lateinit var pref: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binder = DataBindingUtil.setContentView(this, R.layout.order_status_page)
        binder.rvOrderStatus.layoutManager = LinearLayoutManager(applicationContext,RecyclerView.VERTICAL,false)
        binder.rvOrderDescription.layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        viewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        listener()
        setUpView()
        viewModel.fetchOrderDetail()
        observe()
    }



    private fun observe(){
        viewModel.fetchOrderDetailLiveData.observe(this, Observer {
            when(it.type){
                Result.Status.LOADING->{

                }
                Result.Status.SUCCESS->{
                    viewModel.order.allStatus?.let { it1 -> orderAdapter.update(it1) }
                    val items = viewModel.order.items
                    if (items != null) {
                        listAdapter.update(items)
                    }
                }
                Result.Status.ERROR->{
                    binder.root.showError("Unable to fetch Status")
                }
            }
        })
    }


    private fun listener() {
        val closeBtn =binder.orderBackBtn
        closeBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setUpView(){
    pref = PrefManager.getInstance(applicationContext)
        initCart()
        viewModel.orderId = intent.getStringExtra(ORDER_ID)

        binder.rvOrderStatus.apply {
            orderAdapter = OrderStatusAdapter(arrayListOf())
            binder.rvOrderStatus.adapter = orderAdapter
        }

        binder.rvOrderDescription.apply {

            listAdapter = ProductListAdapter( arrayListOf(),
                    ORDER_DESCRIPTION_ITEM_TYPE)
            binder.rvOrderDescription.adapter=listAdapter
        }


    }

    private fun initCart() {
        productViewModel.cart = pref.get(CART)?: Cart()
        productViewModel.initCart()
    }
}