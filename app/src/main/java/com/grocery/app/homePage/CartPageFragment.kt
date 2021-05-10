package com.grocery.app.homePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.adapters.ProductListAdapter
import com.grocery.app.constant.CART_ITEM_TYPE
import com.grocery.app.databinding.CartItemsGroupBinding
import com.grocery.app.fragments.BaseFragment


class CartPageFragment : BaseFragment() {

       lateinit var binder :CartItemsGroupBinding
       private lateinit var ListAdapter: ProductListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder.cartRecyclerView.layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)
        setUpView()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binder= CartItemsGroupBinding.inflate(inflater,container,false)
        return binder.root
    }

    private fun setUpView() {
    binder.cartRecyclerView.apply {
        ListAdapter = ProductListAdapter(arrayListOf(), CART_ITEM_TYPE)
        binder.cartRecyclerView.adapter=ListAdapter
    }
    }

}