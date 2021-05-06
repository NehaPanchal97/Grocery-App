package com.grocery.app.homePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R
import com.grocery.app.databinding.SpecificItemgroupInProductBinding
import com.grocery.app.extensions.showError
import com.grocery.app.extras.Result
import com.grocery.app.fragments.BaseFragment
import com.grocery.app.homePage.adapters.SpecificItemAdapter
import com.grocery.app.models.Category
import com.grocery.app.viewModels.CategoryViewModel
import com.grocery.app.viewModels.ProductViewModel
import kotlinx.android.synthetic.main.specific_itemgroup_in_product.*

class CategoryItemsFragment : BaseFragment() {

    private  lateinit var itemRecyclerViewAdapter: SpecificItemAdapter
    lateinit var binder:SpecificItemgroupInProductBinding
    private lateinit var viewModel:ProductViewModel
    private lateinit var catViewModel:CategoryViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        item_recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        item_recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL))
        item_recyclerView.layoutManager = GridLayoutManager(context,2, RecyclerView.VERTICAL,false)
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        catViewModel = ViewModelProvider(requireActivity()).get(CategoryViewModel::class.java)

        val item = view.findViewById<TextView>(R.id.specific_category)
        catViewModel.catName.observe(viewLifecycleOwner, Observer {
            item.text= it
        })

        itemRecyclerView()
        observe()
        viewModel.filterByCat= Category(id="5ZlyoMBXxjfZLJMAccJk")
        viewModel.fetchProductList()
//        catViewModel.fetchCategoryList()

    }

    private fun observe() {
        viewModel.productListLiveData.observe(viewLifecycleOwner , Observer {
            when(it.type){
               Result.Status.LOADING->{
                  binder.progressBar.show()
               }
                Result.Status.SUCCESS->{
                    binder.progressBar.hide()
                    val products = it.data?: arrayListOf()
                    itemRecyclerViewAdapter.update(products)
                }

                Result.Status.ERROR->{
                    binder.progressBar.hide()
                    binder.root.showError("unable to fetch products")
                }

            }
        })
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binder= SpecificItemgroupInProductBinding.inflate(inflater,container,false)
        return binder.root
    }

    private fun itemRecyclerView() {
        item_recyclerView.apply {

            itemRecyclerViewAdapter = SpecificItemAdapter(arrayListOf())
           binder.itemRecyclerView.adapter=itemRecyclerViewAdapter
        }
    }

}