package com.grocery.app.homePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.grocery.app.databinding.CartItemsGroupBinding
import com.grocery.app.fragments.BaseFragment


class CartPageFragment : BaseFragment() {

       lateinit var binder :CartItemsGroupBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binder= CartItemsGroupBinding.inflate(inflater,container,false)
        return binder.root
    }
}