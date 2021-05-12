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
import com.grocery.app.constant.CART
import com.grocery.app.constant.HOMEPAGE_PRODUCT_TYPE
import com.grocery.app.constant.PRODUCT
import com.grocery.app.databinding.ActivityDetailsPageBinding
import com.grocery.app.extensions.loadImage
import com.grocery.app.extensions.showError
import com.grocery.app.extensions.visible
import com.grocery.app.extras.Result
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.models.Cart
import com.grocery.app.models.Product
import com.grocery.app.utils.PrefManager
import com.grocery.app.viewModels.ProductViewModel

class DetailsPageActivity : AppCompatActivity() {
    private lateinit var listAdapter: ProductListAdapter
    lateinit var binder: ActivityDetailsPageBinding
    private lateinit var viewModel: ProductViewModel
    private lateinit var prefManager: PrefManager
    private var _product
        get() = viewModel.product
        set(value) {
            viewModel.product = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_details_page)

        binder.rvSimilarProduct.layoutManager =
            LinearLayoutManager(applicationContext, RecyclerView.HORIZONTAL, false)
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        setUpView()
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


    private val _itemClickListener = object : OnItemClickListener {
        override fun onItemClick(itemId: Int, position: Int) {
            if (itemId == R.id.iv_add) {
                val product = listAdapter.items.getOrNull(position)
                viewModel.updateCart(product, isAddition = true)
                listAdapter.notifyItemChanged(position)
            } else if (itemId == R.id.iv_remove) {
                val product = listAdapter.items.getOrNull(position)
                viewModel.updateCart(product, isAddition = false)
                listAdapter.notifyItemChanged(position)
            }
        }

    }

    private fun setUpView() {
        //Initialize Cart
        prefManager = PrefManager.getInstance(this)
        initCart()
        _product = intent.getParcelableExtra(PRODUCT) ?: Product()
        binder.rvSimilarProduct.apply {
            listAdapter =
                ProductListAdapter(arrayListOf(), HOMEPAGE_PRODUCT_TYPE, viewModel.cartMap)
            listAdapter.onClickListener = _itemClickListener
            binder.rvSimilarProduct.adapter = listAdapter
        }
        viewModel.cartMap[_product.id]?.count
        onQuantityChange()
        binder.detailsAddBtn.setOnClickListener {
            val productCount = _product.count ?: 0
            _product.count = productCount + 1
            onQuantityChange()
        }
        binder.detailsRemoveBtn.setOnClickListener {
            val productCount = _product.count ?: 0
            if (productCount >= 1) {
                _product.count = productCount - 1
                onQuantityChange()
            }
        }
        binder.tvProductName.text = _product.name
        binder.price.text = _product.price?.toInt().toString()
        binder.tvProductPrice.text = _product.price?.toInt().toString()
        binder.tvDescription.text = _product.description.toString()
        binder.ivItem.loadImage(_product.url)
    }

    private fun initCart() {
        viewModel.cart = prefManager.get(CART) ?: Cart()
        viewModel.initCart()
    }

    private fun onQuantityChange() {
        val count = _product.count ?: 0
        binder.cvAddToCart.visible(count > 0)
        val totalCost = _product.price?.times(count)
        binder.tvProductCount.text = count.toString()
        binder.tvAmount.text = totalCost.toString()
    }
}