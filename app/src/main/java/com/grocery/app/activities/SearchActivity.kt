package com.grocery.app.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R
import com.grocery.app.adapters.ProductListAdapter
import com.grocery.app.auth.UpdateProfileFragment
import com.grocery.app.constant.*
import com.grocery.app.databinding.ActivitySearchBinding
import com.grocery.app.extensions.showError
import com.grocery.app.extras.Result
import com.grocery.app.homePage.CartPageFragment
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.models.Cart
import com.grocery.app.models.Product
import com.grocery.app.utils.PrefManager
import com.grocery.app.viewModels.ProductViewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var itemRvAdapter: ProductListAdapter
    private lateinit var viewModel: ProductViewModel
    lateinit var binder: ActivitySearchBinding
    lateinit var pref: PrefManager
    var product = Product()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_search)


        binder.editTextSearch.requestFocus()
        pref = PrefManager.getInstance(applicationContext)
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
//        binder.productItemRV.addItemDecoration(
//            DividerItemDecoration(
//                applicationContext,
//                DividerItemDecoration.VERTICAL
//            )
//        )
//        binder.productItemRV.addItemDecoration(
//            DividerItemDecoration(
//                applicationContext,
//                DividerItemDecoration.HORIZONTAL
//            )
//        )
        binder.productItemRV.layoutManager =
            GridLayoutManager(applicationContext, 2, RecyclerView.VERTICAL, false)

        itemRecyclerView()
        initCart()

        binder.editTextSearch.doAfterTextChanged { result ->
            if (result?.length ?: 0 > 2) {
                viewModel.fetchProductWithKey(result.toString().toLowerCase())
            } else {
                itemRvAdapter.clearAdapter()

            }
        }
        observe()

        binder.backBtn.setOnClickListener() {
            onBackPressed()
        }
    }

    private fun observe() {
        viewModel.searchProductLiveData.observe(this, Observer { result ->
            when (result.type) {
                Result.Status.LOADING -> {
                }
                Result.Status.SUCCESS -> {
                    val products = result.data ?: arrayListOf()
                    itemRvAdapter.update(false, products)
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
            val product = itemRvAdapter.items.getOrNull(position)
            if (itemId == R.id.iv_add) {
                viewModel.updateCart(product, isAddition = true)
                pref.put(CART, viewModel.cart)
                itemRvAdapter.notifyItemChanged(position)
                fireCartChangeEvent()
                binder.addToCartBtn.setOnClickListener() {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_search, CartPageFragment())
                        .addToBackStack(null)
                        .commit()
                }

            } else if (itemId == R.id.iv_remove) {
                viewModel.updateCart(product, isAddition = false)
                pref.put(CART, viewModel.cart)
                itemRvAdapter.notifyItemChanged(position)
                fireCartChangeEvent()
            } else if (itemId == R.id.itemImage) {
                val intent = Intent(applicationContext, DetailsPageActivity::class.java)
                intent.putExtra(PRODUCT, product)
                startActivity(intent)
            }
        }

    }


    private fun itemRecyclerView() {


        binder.productItemRV.apply {
            itemRvAdapter = ProductListAdapter(
                arrayListOf(),
                HOMEPAGE_PRODUCT_TYPE, viewModel.cartMap
            )
            itemRvAdapter.onClickListener = _itemClickListener
            binder.productItemRV.adapter = itemRvAdapter
        }
    }

    private fun initCart() {
        viewModel.cart = pref.get(CART) ?: Cart()
        viewModel.initCart()
    }


}