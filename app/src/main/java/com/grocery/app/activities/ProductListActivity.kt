package com.grocery.app.activities

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.grocery.app.R
import com.grocery.app.adapters.ProductListAdapter
import com.grocery.app.constant.PRODUCT
import com.grocery.app.databinding.ActivityProductListBinding
import com.grocery.app.extensions.showError
import com.grocery.app.extensions.visible
import com.grocery.app.extras.Result
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.models.Product
import com.grocery.app.viewModels.ProductViewModel
import java.util.ArrayList

class ProductListActivity : BaseActivity() {

    private lateinit var viewModel: ProductViewModel
    private lateinit var binder: ActivityProductListBinding
    private lateinit var listAdapter: ProductListAdapter

    companion object {
        const val EDIT_PRODUCT_REQUEST_CODE = 1278
        const val ADD_PRODUCT_REQUEST_CODE = 1279
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_product_list)
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        setupView()
        observe()
        viewModel.fetchProductList()
    }

    private fun observe() {
        viewModel.productListLiveData.observe(this, Observer {
            when (it.type) {
                Result.Status.LOADING -> loading(true)
                Result.Status.SUCCESS -> {
                    loading(false)
                    listAdapter.update(it.data ?: arrayListOf())
                }
                Result.Status.ERROR -> {
                    loading(false)
                    binder.root.showError(getString(R.string.fetch_product_list_error))
                }
            }
        })
    }

    override fun loading(show: Boolean) {
        binder.progressBar.visible(show)
    }

    private fun setupView() {
        listAdapter = ProductListAdapter(arrayListOf())
        binder.productRv.adapter = listAdapter
        listAdapter.onClickListener = _onClickListener
    }

    private val _onClickListener = object : OnItemClickListener {
        override fun onItemClick(itemId: Int, position: Int) {
            if (itemId == R.id.edit_iv) {
                val intent =
                    Intent(this@ProductListActivity, AddProductActivity::class.java).apply {
                        putExtra(PRODUCT, listAdapter.products[position])
                    }
                startActivityForResult(intent, EDIT_PRODUCT_REQUEST_CODE)
            }
        }
    }
}