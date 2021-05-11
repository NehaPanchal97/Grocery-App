package com.grocery.app.homePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.adapters.ProductListAdapter
import com.grocery.app.constant.CART_ITEM_TYPE
import com.grocery.app.databinding.CartItemsGroupBinding
import com.grocery.app.extensions.showError
import com.grocery.app.extras.Result
import com.grocery.app.fragments.BaseFragment
import com.grocery.app.viewModels.ProductViewModel
import java.util.Observer


class CartPageFragment : BaseFragment() {

       lateinit var binder :CartItemsGroupBinding
       private lateinit var listAdapter: ProductListAdapter
       lateinit var viewModel: ProductViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder.cartRecyclerView.layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        setUpView()
        observe()
        viewModel.fetchProductList()
    }

    private fun observe(){
        viewModel.productListLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it.type) {
                Result.Status.LOADING -> {
                }
                Result.Status.SUCCESS -> {

                    val products = it.data ?: arrayListOf()
                    listAdapter.update(products)
                }

                Result.Status.ERROR -> {

                    binder.root.showError("unable to fetch products")
                }

            }
        })
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binder= CartItemsGroupBinding.inflate(inflater,container,false)
        return binder.root
    }

    private fun setUpView() {
    binder.cartRecyclerView.apply {
        listAdapter = ProductListAdapter(arrayListOf(), CART_ITEM_TYPE)
        binder.cartRecyclerView.adapter=listAdapter
    }
    }

}