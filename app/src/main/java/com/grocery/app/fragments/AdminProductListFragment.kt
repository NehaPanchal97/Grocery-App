package com.grocery.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.grocery.app.R
import com.grocery.app.adapters.ProductListAdapter
import com.grocery.app.constant.ADMIN_PRODUCT_TYPE
import com.grocery.app.constant.Store
import com.grocery.app.contracts.AddProductContract
import com.grocery.app.contracts.UpdateProductContract
import com.grocery.app.customs.OnLoadMoreListener
import com.grocery.app.databinding.FragmentProductListBinding
import com.grocery.app.extensions.*
import com.grocery.app.extras.Result
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.viewModels.CategoryViewModel
import com.grocery.app.viewModels.ProductViewModel

class AdminProductListFragment : BaseFragment(), Toolbar.OnMenuItemClickListener,
    SearchView.OnQueryTextListener {

    private lateinit var viewModel: ProductViewModel
    private lateinit var catViewModel: CategoryViewModel
    private lateinit var binder: FragmentProductListBinding
    private lateinit var listAdapter: ProductListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binder = FragmentProductListBinding.inflate(inflater, container, false)
        return binder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        catViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
        setupView()
        observe()
        fetchProducts()
        catViewModel.fetchCategoryList()
    }

    private fun fetchProducts(initialFetch: Boolean = true) {
        viewModel.fetchProductList(initialFetch, Store.CREATED_AT)
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
        MaterialAlertDialogBuilder(requireContext(), R.style.AppDialogTheme)
            .setTitle(getString(R.string.category))
            .setNeutralButton(
                getString(R.string.clear)
            ) { _, _ ->
                viewModel.filterByCat = null
                refreshList()
            }
            .setPositiveButton(getString(R.string.apply)) { _, _ ->
                refreshList()
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                viewModel.filterByCat = null
            }
            .setSingleChoiceItems(items, checked) { _, which ->
                viewModel.filterByCat = viewModel.catList[which]
            }
            .setCancelable(false)
            .show()
    }

    private fun observe() {
        viewModel.productListLiveData.observe(viewLifecycleOwner, Observer {
            when (it.type) {
                Result.Status.LOADING -> {
                    if (viewModel.loadMore) {
                        listAdapter.addLoader()
                    } else {
                        loading(true)
                        binder.emptyView.root.hide()
                    }
                }
                Result.Status.SUCCESS -> {
                    if (!viewModel.loadMore) {
                        loading(false)
                    }
                    val products = it.data ?: arrayListOf()
                    listAdapter.update(viewModel.loadMore, products)
                    binder.emptyView.root.visible(listAdapter.items.isEmpty())
                }
                Result.Status.ERROR -> {
                    loading(false)
                    binder.root.showError(getString(R.string.fetch_product_list_error))
                }
            }
        })
        catViewModel.catListLiveData.observe(viewLifecycleOwner, Observer {

            if (it.type == Result.Status.SUCCESS) {
                viewModel.catList = it.data ?: arrayListOf()
            }
        })

    }

    override fun loading(show: Boolean) {
        binder.progressBar.visible(show)
    }

    private fun setupView() {
        listAdapter = ProductListAdapter(arrayListOf(), ADMIN_PRODUCT_TYPE)
        binder.productRv.adapter = listAdapter
        listAdapter.onClickListener = _onClickListener

        //Toolbar
        binder.emptyView.emptyTv.text = getString(R.string.no_product_available)
        binder.productRv.addOnScrollListener(_onLoadMoreListener)
    }

    private fun refreshList() {
        listAdapter.clearAdapter()
        fetchProducts()
    }

    private val _onLoadMoreListener = object : OnLoadMoreListener() {

        override val hasMore: Boolean
            get() = viewModel.hasMoreProduct

        override val isRequesting: Boolean
            get() = viewModel.productListLiveData.value?.type == Result.Status.LOADING

        override fun onLoadMore() {
            fetchProducts(initialFetch = false)
        }

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

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.add) {
            addProduct.launch(null)
        }
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        activity?.hideKeyboard()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val textLength = newText?.trim()?.length ?: 0
        if (textLength > 2) {
            viewModel.searchKey = newText?.toLowerCase()
            fetchProducts()
        } else if (textLength == 0) {
            viewModel.searchKey = null
            fetchProducts()
        }
        return true
    }
}