package com.grocery.app.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R
import com.grocery.app.adapters.ProductListAdapter
import com.grocery.app.constant.HOMEPAGE_PRODUCT_TYPE
import com.grocery.app.databinding.ActivityDetailsPageBinding
import com.grocery.app.extensions.showError
import com.grocery.app.extras.Result
import com.grocery.app.viewModels.ProductViewModel

class DetailsPageActivity: AppCompatActivity() {
    private lateinit var listAdapter: ProductListAdapter
    lateinit var binder: ActivityDetailsPageBinding
    private lateinit var viewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binder= DataBindingUtil.setContentView(this,R.layout.activity_details_page)

        binder.rvSimilarProduct.layoutManager =
            LinearLayoutManager(applicationContext, RecyclerView.HORIZONTAL, false)
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        itemRecyclerView()
        observe()
        viewModel.fetchProductList()

    }


    private fun observe() {
        viewModel.productListLiveData.observe(this, Observer {
            when (it.type) {
                Result.Status.LOADING -> {
                }
                Result.Status.SUCCESS -> {
                    val products = it.data ?: arrayListOf()
                    listAdapter.update(products)
                }

                Result.Status.ERROR -> {
                    binder.root.showError("unable to fetch products")
                }

            }
        })
    }

    private fun itemRecyclerView() {
        binder.rvSimilarProduct.apply {

            listAdapter = ProductListAdapter(arrayListOf(), HOMEPAGE_PRODUCT_TYPE)
//            itemRecyclerViewAdapter = SpecificItemAdapter(arrayListOf(), viewModel.cartMap)
//            itemRecyclerViewAdapter.itemClickListener = _itemClickListener
            binder.rvSimilarProduct.adapter = listAdapter
        }
    }
}