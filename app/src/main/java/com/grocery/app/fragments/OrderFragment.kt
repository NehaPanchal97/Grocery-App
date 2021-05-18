package com.grocery.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.adapters.ProductListAdapter
import com.grocery.app.constant.CART
import com.grocery.app.constant.ORDER_ITEMS
import com.grocery.app.databinding.FragmentOrderBinding
import com.grocery.app.models.Cart
import com.grocery.app.utils.PrefManager
import com.grocery.app.viewModels.ProductViewModel


class OrderFragment : BaseFragment() {
    lateinit var binder: FragmentOrderBinding
    lateinit var viewModel: ProductViewModel
    lateinit var pref: PrefManager
    lateinit var listAdapter: ProductListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder.orderRv.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        setUpView()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binder = FragmentOrderBinding.inflate(inflater, container, false)
        return binder.root
    }

    private fun setUpView(){
        pref = PrefManager.getInstance(requireContext())
        initCart()
        binder.orderRv.apply {
            listAdapter = ProductListAdapter(viewModel.cart.items?: arrayListOf(), ORDER_ITEMS,viewModel.cartMap)
            binder.orderRv.adapter = listAdapter
        }
    }

    private fun initCart(){
        viewModel.cart = pref.get(CART)?: Cart()
        viewModel.initCart()
    }

}