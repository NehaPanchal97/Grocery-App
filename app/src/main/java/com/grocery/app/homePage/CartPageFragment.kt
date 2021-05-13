package com.grocery.app.homePage

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R
import com.grocery.app.adapters.ProductListAdapter
import com.grocery.app.constant.CART
import com.grocery.app.constant.CART_ITEM_TYPE
import com.grocery.app.databinding.CartItemsGroupBinding
import com.grocery.app.extensions.showError
import com.grocery.app.extensions.visible
import com.grocery.app.extras.Result
import com.grocery.app.fragments.BaseFragment
import com.grocery.app.fragments.OrderFragment
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.models.Cart
import com.grocery.app.models.Product
import com.grocery.app.utils.PrefManager
import com.grocery.app.viewModels.ProductViewModel
import kotlinx.android.synthetic.main.cart_item.*
import kotlinx.android.synthetic.main.cart_items_group.view.*
import java.util.Observer


class CartPageFragment : BaseFragment() {

       lateinit var binder :CartItemsGroupBinding
       private lateinit var listAdapter: ProductListAdapter
       lateinit var viewModel: ProductViewModel
       lateinit var pref:PrefManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder.cartRecyclerView.layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        setUpView()
        checkoutBtn()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binder= CartItemsGroupBinding.inflate(inflater,container,false)
        return binder.root
    }

    @SuppressLint("SetTextI18n")
    private fun setUpView() {
        pref= PrefManager.getInstance(requireContext())
        initCart()
        val cartTotal = viewModel.cart.total?.toString()?:"0"
        binder.cartAmount.text = "Total : \$$cartTotal"
        val total = viewModel.cart.total

        if (total != null) {
            binder.checkoutContainer.visible(total>0)
        }
        if(viewModel.cart.items?.isEmpty() !=true){
            binder.cartRecyclerView.apply {
                listAdapter = ProductListAdapter(viewModel.cart.items?: arrayListOf(), CART_ITEM_TYPE,viewModel.cartMap)
                binder.cartRecyclerView.itemAnimator = null
                listAdapter.onClickListener = _itemClickListener
                binder.cartRecyclerView.adapter=listAdapter
            }
        }else
            binder.root.showError("Cart is empty")

    }

    private val _itemClickListener = object : OnItemClickListener {
        @SuppressLint("SetTextI18n")
        override fun onItemClick(itemId: Int, position: Int) {
            if (itemId == R.id.iv_cart_add) {
                val product = listAdapter.items.getOrNull(position)
                viewModel.updateCart(product, isAddition = true)
                listAdapter.notifyItemChanged(position)
                onTotalChange()
            } else if (itemId == R.id.iv_cart_remove) {
                val product = listAdapter.items.getOrNull(position)
                viewModel.updateCart(product, isAddition = false)
                listAdapter.notifyItemChanged(position)
                onTotalChange()
            }

        }

    }

    @SuppressLint("SetTextI18n")
    private fun onTotalChange(){
        val cartTotal = viewModel.cart.total?.toString()?:"0"
        val total = viewModel.cart.total
        binder.cartAmount.text = "Total : \$$cartTotal"
        if (total != null) {
            binder.checkoutContainer.visible(total>0)
        }
    }


    private fun initCart() {
       viewModel.cart = pref.get(CART)?: Cart()
        viewModel.initCart()
    }

    private fun checkoutBtn(){
        val checkout = binder.checkoutBtn
        checkout.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.add(R.id.fragment_container,OrderFragment() )
                ?.addToBackStack(null)
                ?.commit()
        }
    }

}