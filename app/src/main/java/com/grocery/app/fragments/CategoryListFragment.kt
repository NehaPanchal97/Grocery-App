package com.grocery.app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.grocery.app.R
import com.grocery.app.adapters.CategoryListAdapter
import com.grocery.app.contracts.AddCategoryContract
import com.grocery.app.contracts.UpdateCategoryContract
import com.grocery.app.databinding.FragmentCategoryListBinding
import com.grocery.app.extensions.showError
import com.grocery.app.extensions.showSuccess
import com.grocery.app.viewModels.CategoryViewModel
import com.grocery.app.extras.Result.Status
import com.grocery.app.listeners.OnItemClickListener

class CategoryListFragment : BaseFragment(), Toolbar.OnMenuItemClickListener {

    private lateinit var binder: FragmentCategoryListBinding
    private lateinit var viewModel: CategoryViewModel
    private lateinit var listAdapter: CategoryListAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binder = FragmentCategoryListBinding.inflate(inflater, container, false)
        return binder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)

        setupView()
        observe()
        viewModel.fetchCategoryList()
    }

    private fun observe() {
        viewModel.catListLiveData.observe(viewLifecycleOwner, Observer {
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

    private fun setupView() {
        listAdapter = CategoryListAdapter(arrayListOf())
            .apply { onClickListener = _itemClickListener }
        binder.categoryRv.adapter = listAdapter
    }

    private val _itemClickListener = object : OnItemClickListener {
        override fun onItemClick(itemId: Int, position: Int) {
            updateCategoryResult.launch(listAdapter.categories.getOrNull(position))
        }

    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
//            R.id.cart -> startActivity(
//                Intent(
//                    requireContext(),
//                    ProductListFragment::class.java
//                )
//            )
            R.id.add -> addCategoryResult.launch(null)
        }
        return true
    }
}