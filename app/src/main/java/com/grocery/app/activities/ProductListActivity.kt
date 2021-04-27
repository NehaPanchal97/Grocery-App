package com.grocery.app.activities

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.grocery.app.R
import com.grocery.app.adapters.ProductListAdapter
import com.grocery.app.contracts.AddProductContract
import com.grocery.app.contracts.UpdateProductContract
import com.grocery.app.databinding.ActivityProductListBinding
import com.grocery.app.extensions.hide
import com.grocery.app.extensions.showError
import com.grocery.app.extensions.showSuccess
import com.grocery.app.extensions.visible
import com.grocery.app.extras.Result
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.viewModels.CategoryViewModel
import com.grocery.app.viewModels.ProductViewModel

class ProductListActivity : BaseActivity() {

    private lateinit var viewModel: ProductViewModel
    private lateinit var catViewModel: CategoryViewModel
    private lateinit var binder: ActivityProductListBinding
    private lateinit var listAdapter: ProductListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_product_list)
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        catViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
        setupView()
        observe()
        viewModel.fetchProductList()
        catViewModel.fetchCategoryList()
    }

    private val _menuItemClick = Toolbar.OnMenuItemClickListener {
        when (it.itemId) {
            R.id.add_product -> addProduct.launch(null)
            R.id.filter_product -> showFilterDialog()
        }
        return@OnMenuItemClickListener true
    }

    private fun showFilterDialog() {
        if (viewModel.catList.isEmpty()) {
            return
        }
        val items = viewModel.catList.map { it.name }.toTypedArray()
        val checked = items.indexOfFirst { it == viewModel.filterByCat?.name }
        MaterialAlertDialogBuilder(this, R.style.AppDialogTheme)
            .setTitle("Category")
            .setNeutralButton(
                "Clear"
            ) { _, _ ->
                viewModel.filterByCat = null
                refreshList()
            }
            .setPositiveButton("Apply") { dialog, which ->
                refreshList()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                viewModel.filterByCat = null
            }
            .setSingleChoiceItems(items, checked) { dialog, which ->
                viewModel.filterByCat = viewModel.catList[which]
            }
            .setCancelable(false)
            .show()
    }

    private fun observe() {
        viewModel.productListLiveData.observe(this, Observer {
            when (it.type) {
                Result.Status.LOADING -> {
                    loading(true)
                    binder.emptyView.root.hide()
                }
                Result.Status.SUCCESS -> {
                    loading(false)
                    val products = it.data ?: arrayListOf()
                    binder.emptyView.root.visible(products.isEmpty())
                    listAdapter.update(products)

                }
                Result.Status.ERROR -> {
                    loading(false)
                    binder.root.showError(getString(R.string.fetch_product_list_error))
                }
            }
        })
        catViewModel.catListLiveData.observe(this, Observer {

            if (it.type == Result.Status.SUCCESS) {
                viewModel.catList = it.data ?: arrayListOf()
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

        //Toolbar
        binder.toolBar.inflateMenu(R.menu.product_list_menu)
        binder.toolBar.setOnMenuItemClickListener(_menuItemClick)
        binder.emptyView.emptyTv.text = getString(R.string.no_product_available)
    }

    private fun refreshList() {
        listAdapter.clearAdapter()
        viewModel.fetchProductList()
    }

    private val updateProduct =
        registerForActivityResult(UpdateProductContract()) { result ->
            if (result) {
                binder.root.showSuccess(getString(R.string.product_updated_msg))
                refreshList()
            }
        }
    private val addProduct =
        registerForActivityResult(AddProductContract()) { result ->
            if (result) {
                binder.root.showSuccess(getString(R.string.product_created_msg))
                refreshList()
            }
        }

    private val _onClickListener = object : OnItemClickListener {
        override fun onItemClick(itemId: Int, position: Int) {
            if (itemId == R.id.edit_iv) {
                updateProduct.launch(listAdapter.products[position])
            }
        }
    }
}