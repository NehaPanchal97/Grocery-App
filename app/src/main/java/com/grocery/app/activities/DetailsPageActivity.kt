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
import com.grocery.app.constant.CART
import com.grocery.app.constant.CART_CHANGE
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
        viewModel.fetchProductWithTag()
        binder.imBackBtn.setOnClickListener {
            onBackPressed()
        }
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
                    }
                    else{
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
            if (itemId == R.id.iv_add) {
                onCartUpdated(true, position)
            } else if (itemId == R.id.iv_remove) {
                onCartUpdated(false, position)
            }
        }

    }

    private fun onCartUpdated(isAdded: Boolean, position: Int) {
        val product = listAdapter.items.getOrNull(position)
        viewModel.updateCart(product, isAddition = isAdded)
        listAdapter.notifyItemChanged(position)
        fireCartChangeEvent()
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
        binder.detailsAddBtn.setOnClickListener {
            viewModel.updateCart(_product, true)
            prefManager.put(CART, viewModel.cart)
            onQuantityChange()
            fireCartChangeEvent()
        }
        binder.detailsRemoveBtn.setOnClickListener {
            val productCount = viewModel.cartMap[_product.id]?.count ?: 0
            if (productCount >= 1) {
                viewModel.updateCart(_product, false)
                prefManager.put(CART, viewModel.cart)
                onQuantityChange()
                fireCartChangeEvent()
            }
        }
        binder.tvProductName.text = _product.name
        binder.tvPriceWithUnit.text = _product.price?.toInt().toString()
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
        val totalCost = product?.total?.toInt() ?: 0
        binder.tvProductCount.text = count.toString()
        binder.tvProductPrice.visible(totalCost != 0)
        binder.tvProductPrice.text =
            String.format(getString(R.string.rs_symbol), totalCost.toString())

    }
}