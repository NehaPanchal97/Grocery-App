package com.grocery.app.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.Query
import com.grocery.app.R
import com.grocery.app.adapters.ProductListAdapter
import com.grocery.app.constant.DISCOUNT
import com.grocery.app.constant.DISCOUNT_ITEM
import com.grocery.app.constant.PRODUCT
import com.grocery.app.constant.TITLE
import com.grocery.app.databinding.ActivityDiscountPageBinding
import com.grocery.app.extensions.showError
import com.grocery.app.extras.Result
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.viewModels.ProductViewModel

class DiscountPageActivity : AppCompatActivity(), OnItemClickListener {

    lateinit var binder: ActivityDiscountPageBinding
    lateinit var viewModel: ProductViewModel
    private lateinit var listAdapter: ProductListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binder = DataBindingUtil.setContentView(this, R.layout.activity_discount_page)
        binder.discountPageRv.layoutManager =
            LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        setUpView()
        dropDown()
        observe()
        binder.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setUpView() {
        val discount = intent?.getDoubleExtra(DISCOUNT, -1.0) ?: -1.0
        if (discount >= 0) {
            viewModel.discount = discount
        }
        val title = intent?.getStringExtra(TITLE)
        binder.tvTitle.text = title
        binder.discountPageRv.apply {
            listAdapter = ProductListAdapter(arrayListOf(), DISCOUNT_ITEM)
            adapter = listAdapter
        }
        listAdapter.onClickListener = this
    }

    private fun observe() {
        viewModel.productListLiveData.observe(this, Observer {
            when (it.type) {
                Result.Status.LOADING -> {
                  // nothing to load
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

    private fun dropDown() {
        // access the items of the dropdown list
        val priceList = resources.getStringArray(R.array.discountDropDown)

        //access the spinner
        val spinner = binder.spinnerView
        val adapter =
            ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, priceList)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.orderBy =
                    if (position == 0) Query.Direction.ASCENDING
                    else Query.Direction.DESCENDING
                viewModel.fetchProductList()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // access default
            }
        }
    }

    companion object {
        fun newIntent(context: Context, discount: Double, title: String): Intent {
            val intent = Intent(context, DiscountPageActivity::class.java)
            intent.putExtra(DISCOUNT, discount)
            intent.putExtra(TITLE, title)
            return intent
        }
    }

    override fun onItemClick(itemId: Int, position: Int) {
        val product = listAdapter.items.getOrNull(position)
        val intent = Intent(this,DetailsPageActivity::class.java)
        intent.putExtra(PRODUCT,product)
        startActivity(intent)
    }

}