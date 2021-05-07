package com.grocery.app.homePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.databinding.CartItemsGroupBinding
import com.grocery.app.fragments.BaseFragment
import com.grocery.app.homePage.adapters.CartListAdapter


class CartPageFragment : BaseFragment() {

       lateinit var binder :CartItemsGroupBinding
       private lateinit var ListAdapter: CartListAdapter

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
        ListAdapter = CartListAdapter(arrayListOf())
        binder.cartRecyclerView.adapter=ListAdapter
    }
    }

}