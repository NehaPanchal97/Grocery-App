package com.grocery.app.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.grocery.app.R
import com.grocery.app.adapters.ProductListAdapter
import com.grocery.app.constant.*
import com.grocery.app.databinding.ActivitySearchBinding
import com.grocery.app.extensions.showError
import com.grocery.app.extras.Result
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.models.Cart
import com.grocery.app.models.Product
import com.grocery.app.utils.PrefManager
import com.grocery.app.viewModels.ProductViewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var itemRvAdapter : ProductListAdapter
    private lateinit var viewModel: ProductViewModel
    lateinit var binder : ActivitySearchBinding
    lateinit var pref: PrefManager
    var product = Product()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this,R.layout.activity_search)

        pref = PrefManager.getInstance(applicationContext)
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        binder.productItemRV.addItemDecoration(
            DividerItemDecoration(
               applicationContext,
                DividerItemDecoration.VERTICAL
            )
        )
        binder.productItemRV.addItemDecoration(
            DividerItemDecoration(
               applicationContext,
                DividerItemDecoration.HORIZONTAL
            )
        )
        binder.productItemRV.layoutManager =
            GridLayoutManager(applicationContext, 2, RecyclerView.VERTICAL, false)

        itemRecyclerView()
        initCart()

        binder.editTextSearch.doAfterTextChanged { result ->
            if (result?.length?: 0 > 2 ) {
                viewModel.fetchProductWithKey(result.toString())
            }
            else {
                itemRvAdapter.clearAdapter()
            }
        }
        observe()

    }

    private fun observe() {
        viewModel.productWithKeyLiveData.observe(this, Observer { result ->
            when (result.type) {
                Result.Status.LOADING -> {
                }
                Result.Status.SUCCESS -> {
                        val products = result.data ?: arrayListOf()
                        itemRvAdapter.update(false,products)
                }

                Result.Status.ERROR -> {
                    binder.root.showError("unable to fetch products")
                }

            }
        })
    }

    private fun fireOrderCreatedEvent() {
        LocalBroadcastManager.getInstance(applicationContext)
            .sendBroadcast(Intent(CART_CHANGE))
    }

    private val _itemClickListener = object : OnItemClickListener {
        override fun onItemClick(itemId: Int, position: Int) {
            val product = itemRvAdapter.items.getOrNull(position)
            if (itemId == R.id.iv_add) {
                viewModel.updateCart(product, isAddition = true)
                itemRvAdapter.notifyItemChanged(position)

            } else if (itemId == R.id.iv_remove) {
                viewModel.updateCart(product, isAddition = false)
                itemRvAdapter.notifyItemChanged(position)
            } else if (itemId == R.id.itemImage) {
                val intent = Intent(applicationContext,DetailsPageActivity::class.java)
                intent.putExtra(PRODUCT,product)
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