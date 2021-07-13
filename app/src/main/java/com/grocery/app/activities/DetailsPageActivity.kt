package com.grocery.app.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R
import com.grocery.app.adapters.ProductListAdapter
import com.grocery.app.constant.*
import com.grocery.app.databinding.ActivityDetailsPageBinding
import com.grocery.app.extensions.loadImage
import com.grocery.app.extensions.percentage
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
        viewModel.fetchProductWithTag()
        binder.imBackBtn.setOnClickListener {
            onBackPressed()
        }
        binder.product = _product
        binder.executePendingBindings()
    }


    private fun observe() {
        viewModel.similarListLiveData.observe(this, Observer {
            when (it.type) {
                Result.Status.LOADING -> {
                    binder.detailsPageProgressBar.show()
                }
                Result.Status.SUCCESS -> {
                    val products = it.data
                    if (!products.isNullOrEmpty()) {
                        listAdapter.update(false, products)
                        binder.rvCardView.visible(true)
                        binder.detailsPageProgressBar.hide()
                    } else {
                        binder.rvCardView.visible(false)
                    }
                }

                Result.Status.ERROR -> {
                    binder.root.showError("unable to fetch products")
                }

            }
        })
    }

    private fun fireCartChangeEvent() {
        LocalBroadcastManager.getInstance(this)
            .sendBroadcast(Intent(CART_CHANGE))
    }


    private val _itemClickListener = object : OnItemClickListener {
        override fun onItemClick(itemId: Int, position: Int) {

            val cartAction = if (itemId == R.id.iv_add) CartAction.QUANTITY_INCREASED
                                         else CartAction.QUANTITY_DECREASED
            val product = listAdapter.items.getOrNull(position)
            viewModel.updateCart(product, cartAction)
            prefManager.put(CART, viewModel.cart)
            listAdapter.notifyItemChanged(position)
            fireCartChangeEvent()
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
            fireCartChangeEvent()
        }
        onQuantityChange()

        binder.btnAddToCart.setOnClickListener {
            viewModel.updateCart(_product)
            prefManager.put(CART, viewModel.cart)
            onQuantityChange()
            fireCartChangeEvent()
        }
        binder.detailsAddBtn.setOnClickListener {
            viewModel.updateCart(_product)
            prefManager.put(CART, viewModel.cart)
            onQuantityChange()
            fireCartChangeEvent()
        }
        binder.detailsRemoveBtn.setOnClickListener {
            val productCount = viewModel.cartMap[_product.id]?.count ?: 0
            if (productCount >= 1) {
                viewModel.updateCart(_product, CartAction.QUANTITY_DECREASED)
                prefManager.put(CART, viewModel.cart)
                onQuantityChange()
                fireCartChangeEvent()
            }
        }
        binder.tvProductName.text = _product.name
        binder.tvDescription.text = _product.description.toString()
        binder.ivItem.loadImage(_product.url)
    }

    private fun initCart() {
        viewModel.cart = prefManager.get(CART) ?: Cart()
        viewModel.initCart()
    }

    private fun onQuantityChange() {
        val product = viewModel.cartMap[_product.id]
        val count = product?.count ?: 0
        if (count == 0) {
            binder.btnAddToCart.visible(true)
            binder.containerWithCount.visible(false)
        } else {
            binder.btnAddToCart.visible(false)
            binder.containerWithCount.visible(true)
        }

        val discount = product?.discount ?: 0.0
        val actualPrice = product?.price ?: 0.0
        val discountedPrice = actualPrice - actualPrice.percentage(discount)
        binder.tvProductCount.text = count.toString()
        if (discountedPrice > 0) {
            binder.tvProductPrice.visible(discountedPrice.toInt() != 0)
            val total = count.times(discountedPrice)
            binder.tvProductPrice.text = getString(R.string.rs_symbol, total.toString())
        } else {
            binder.tvProductPrice.visible(actualPrice.toInt() != 0)
            val total = count.times(actualPrice)
            binder.tvProductPrice.text =
                getString(R.string.rs_symbol, total.toString())
        }
    }
}