package com.grocery.app.activities

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.grocery.app.R
import com.grocery.app.adapters.CategoryListAdapter
import com.grocery.app.databinding.CategoryListActivityBinding
import com.grocery.app.extensions.showError
import com.grocery.app.viewModels.CategoryViewModel
import com.grocery.app.extras.Result.Status

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
                    listAdapter.updateAdapter(it.data?: arrayListOf())
                }
                else -> {
                    binder.progressBar.hide()
                    binder.root.showError("Unable to fetch Categories.")
                }
            }
        })
    }

    private fun setupView() {
        binder.toolBar.setNavigationIcon(R.drawable.ic_back)
        binder.toolBar.setNavigationOnClickListener { onBackPressed() }
        listAdapter = CategoryListAdapter(arrayListOf())
        binder.categoryRv.adapter = listAdapter
    }
}