package com.grocery.app.activities

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R
import com.grocery.app.adapters.ProductListAdapter
import com.grocery.app.constant.*
import com.grocery.app.databinding.ActivitySearchBinding
import com.grocery.app.extensions.showError
import com.grocery.app.extensions.visible
import com.grocery.app.extras.Result
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.models.Cart
import com.grocery.app.models.Product
import com.grocery.app.utils.PrefManager
import com.grocery.app.viewModels.ProductViewModel
import kotlinx.android.synthetic.main.activity_search.*
import java.util.*


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
        binder.productItemRV.layoutManager =
            GridLayoutManager(applicationContext, 2, RecyclerView.VERTICAL, false)

        itemRecyclerView()
        initCart()
        searchCartCount()

        binder.editTextSearch.doAfterTextChanged { result ->
            if (result?.length ?: 0 > 2) {
                editTextSearch.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        editTextSearch.clearFocus()
                        val inputMethod: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethod.hideSoftInputFromWindow(editTextSearch.windowToken, 0)
                        return@OnEditorActionListener true
                    }
                    false
                })
                viewModel.fetchProductWithKey(result.toString().toLowerCase(Locale.ROOT))
            } else {
                itemRvAdapter.clearAdapter()

            }
        }
        observe()

        binder.backBtn.setOnClickListener {
            onBackPressed()
        }
        onCartPressed()
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: ""
            if (action == CART_CHANGE) {
                if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                    resetCart()

                } else {
                    viewModel.cartUpdated = true
                }

            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (viewModel.cartUpdated) {
            viewModel.cartUpdated = false
            resetCart()
        }
    }

    private fun resetCart() {
        val cart = pref.get(CART) ?: Cart()
        viewModel.initCartWith(cart)
        searchCartCount()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(applicationContext)
            .unregisterReceiver(receiver)
    }

    private fun observe() {
        val filter = IntentFilter()
        filter.addAction(CART_CHANGE)
        LocalBroadcastManager.getInstance(applicationContext)
            .registerReceiver(receiver, filter)

        viewModel.searchProductLiveData.observe(this, Observer { result ->
            when (result.type) {
                Result.Status.LOADING -> {
                }
                Result.Status.SUCCESS -> {
                    val products = result.data ?: arrayListOf()
                    if (products.isNotEmpty()) {
                        itemRvAdapter.update(false, products)
                        binder.emptyImage.visible(false)
                        binder.emptyText.visible(false)
                    } else {
                        binder.emptyText.visible(true)
                        binder.emptyImage.visible(true)
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
            val product = itemRvAdapter.items.getOrNull(position)
            when (itemId) {
                R.id.iv_add -> {
                    viewModel.updateCart(product)
                    pref.put(CART, viewModel.cart)
                    itemRvAdapter.notifyItemChanged(position)
                    fireCartChangeEvent()
                }
                R.id.iv_remove -> {
                    viewModel.updateCart(product, CartAction.QUANTITY_DECREASED)
                    pref.put(CART, viewModel.cart)
                    itemRvAdapter.notifyItemChanged(position)
                    fireCartChangeEvent()
                }
                R.id.itemImage -> {
                    val intent = Intent(applicationContext, DetailsPageActivity::class.java)
                    intent.putExtra(PRODUCT, product)
                    startActivity(intent)
                }
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

    private fun onCartPressed(){
        binder.cartIcon.setOnClickListener {
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun searchCartCount() {
        val cartCount = viewModel.cart.items?.size ?: 0
        val count = binder.searchCartBadge
        count.text = cartCount.toString()
        binder.searchCartBadge.visible(cartCount > 0)
    }

}