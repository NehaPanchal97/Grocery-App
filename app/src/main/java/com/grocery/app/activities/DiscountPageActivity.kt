package com.grocery.app.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R
import com.grocery.app.adapters.ProductListAdapter
import com.grocery.app.constant.DISCOUNT
import com.grocery.app.constant.DISCOUNT_ITEM
import com.grocery.app.constant.TITLE
import com.grocery.app.databinding.ActivityDiscountPageBinding
import com.grocery.app.extensions.showError
import com.grocery.app.extensions.visible
import com.grocery.app.extras.Result
import com.grocery.app.viewModels.ProductViewModel

class DiscountPageActivity : AppCompatActivity() {

    lateinit var binder: ActivityDiscountPageBinding
    lateinit var viewModel: ProductViewModel
    private lateinit var listAdapter: ProductListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binder = DataBindingUtil.setContentView(this,R.layout.activity_discount_page)
        binder.discountPageRv.layoutManager = LinearLayoutManager(applicationContext,RecyclerView.VERTICAL,false)
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        setUpView()
        observe()
        viewModel.fetchProductList()
        binder.backBtn.setOnClickListener {
            onBackPressed()
        }

    }

    private fun setUpView(){
        val discount = intent?.getDoubleExtra(DISCOUNT, -1.0) ?: -1.0
        if (discount >= 0){
            viewModel.discount = discount
        }
        val title = intent?.getStringExtra(TITLE)
        binder.tvTitle.text = title
        binder.discountPageRv.apply {
            listAdapter = ProductListAdapter(arrayListOf(), DISCOUNT_ITEM)
            adapter = listAdapter
        }
    }

    private fun observe(){
        viewModel.productListLiveData.observe(this, Observer {
            when (it.type) {
                Result.Status.LOADING -> {

                }
                Result.Status.SUCCESS -> {

                    val products = it.data ?: arrayListOf()
                    listAdapter.update(false, products)
                }

                Result.Status.ERROR -> {
                    binder.root.showError("unable to fetch products")
                }

            }
        })
    }

    companion object{
        fun newIntent(context: Context,discount:Double,title:String) : Intent {
            val intent =  Intent(context,DiscountPageActivity::class.java)
            intent.putExtra(DISCOUNT,discount)
            intent.putExtra(TITLE,title)
            return intent
        }
    }

}