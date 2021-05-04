package com.grocery.app.homePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.homePage.adapters.ProductItemsAdapter
import com.grocery.app.homePage.dataModel.ItemData
import com.grocery.app.R
import com.grocery.app.databinding.ProductItemsGroupBinding
import com.grocery.app.extensions.showError
import com.grocery.app.extras.Result
import com.grocery.app.fragments.BaseFragment
import com.grocery.app.viewModels.CategoryViewModel
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.product_items_group.*

class CategoryTypesFragment : BaseFragment() {

    private lateinit var binder:ProductItemsGroupBinding
    lateinit var productRecyclerViewAdapter: ProductItemsAdapter
    lateinit var viewModel: CategoryViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
        product_recyclerView.layoutManager =
                GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
        startRecyclerView()
        observe()
        viewModel.fetchCategoryList()
    }

    private fun observe() {
        viewModel.catListLiveData.observe(viewLifecycleOwner, Observer {
            when (it.type) {
                Result.Status.LOADING -> {
                    binder.catProgressBar.show()
                }
                Result.Status.SUCCESS -> {
                    binder.catProgressBar.hide()
                    productRecyclerViewAdapter.updateCategory(it.data?: arrayListOf())
                }
                else ->{
                   binder.catProgressBar.hide()
                    binder.root.showError("Unable to fetch categories")
                }
            }
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binder= ProductItemsGroupBinding.inflate(inflater,container,false)
        return binder.root

    }


    private fun startRecyclerView() {
        product_recyclerView.apply {
            productRecyclerViewAdapter = ProductItemsAdapter(arrayListOf())
           binder.productRecyclerView.adapter = productRecyclerViewAdapter
        }
    }


}