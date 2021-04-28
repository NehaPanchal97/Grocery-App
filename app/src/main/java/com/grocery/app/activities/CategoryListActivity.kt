package com.grocery.app.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.grocery.app.R
import com.grocery.app.adapters.CategoryListAdapter
import com.grocery.app.contracts.AddCategoryContract
import com.grocery.app.contracts.UpdateCategoryContract
import com.grocery.app.databinding.CategoryListActivityBinding
import com.grocery.app.extensions.showError
import com.grocery.app.extensions.showSuccess
import com.grocery.app.viewModels.CategoryViewModel
import com.grocery.app.extras.Result.Status
import com.grocery.app.listeners.OnItemClickListener

class CategoryListActivity : BaseActivity() {

    private lateinit var binder: CategoryListActivityBinding
    private lateinit var viewModel: CategoryViewModel
    private lateinit var listAdapter: CategoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.category_list_activity)
        viewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)

        setupView()
        observe()
        viewModel.fetchCategoryList()
    }

    private fun observe() {
        viewModel.catListLiveData.observe(this, Observer {
            when (it.type) {
                Status.LOADING -> {
                    binder.progressBar.show()
                }
                Status.SUCCESS -> {
                    binder.progressBar.hide()
                    listAdapter.updateAdapter(it.data ?: arrayListOf())
                }
                else -> {
                    binder.progressBar.hide()
                    binder.root.showError("Unable to fetch Categories.")
                }
            }
        })
    }

    private val addCategoryResult =
        registerForActivityResult(AddCategoryContract()) { result ->
            if (result) {
                refreshList()
                binder.root.showSuccess(getString(R.string.cat_added_msg))
            }
        }

    private val updateCategoryResult =
        registerForActivityResult(UpdateCategoryContract()) { result ->
            if (result) {
                refreshList()
                binder.root.showSuccess(getString(R.string.cat_updated_msg))
            }
        }

    private fun refreshList() {
        listAdapter.clearAdapter()
        viewModel.fetchCategoryList()
    }

    private val _menuItemClick = Toolbar.OnMenuItemClickListener {
        when (it.itemId) {
            R.id.products -> startActivity(Intent(this, ProductListActivity::class.java))
            R.id.add_category -> addCategoryResult.launch(null)
        }
        return@OnMenuItemClickListener true
    }

    private fun setupView() {
        setupToolbar(binder.toolBar)
        binder.toolBar.setOnMenuItemClickListener(_menuItemClick)
        listAdapter = CategoryListAdapter(arrayListOf())
            .apply { onClickListener = _itemClickListener }
        binder.categoryRv.adapter = listAdapter
    }

    private val _itemClickListener = object : OnItemClickListener {
        override fun onItemClick(itemId: Int, position: Int) {
            updateCategoryResult.launch(listAdapter.categories.getOrNull(position))
        }

    }
}