package com.grocery.app.homePage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R
import com.grocery.app.adapters.ProductListAdapter
import com.grocery.app.constant.CART
import com.grocery.app.constant.CART_ITEM_TYPE
import com.grocery.app.databinding.CartItemsGroupBinding
import com.grocery.app.extensions.visible
import com.grocery.app.fragments.BaseFragment
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.models.Cart
import com.grocery.app.utils.PrefManager
import com.grocery.app.viewModels.ProductViewModel


class CartPageFragment : BaseFragment() {

       lateinit var binder :CartItemsGroupBinding
       private lateinit var listAdapter: ProductListAdapter
       lateinit var viewModel: ProductViewModel
       lateinit var pref:PrefManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder.cartRecyclerView.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        setUpView()
        listener()
        checkoutBtn()
    }

    private fun listener(){
        val closeBtn =binder.cartBackBtn
        closeBtn.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun checkoutBtn(){
        val checkout = binder.checkoutBtn
        checkout.setOnClickListener {

            val intent = Intent(activity, OrderStatusPageActivity::class.java)
            startActivity(intent)



        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binder= CartItemsGroupBinding.inflate(inflater, container, false)
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
            binder.checkoutContainer.visible(total > 0)
        }
        if(viewModel.cart.items?.isEmpty() !=true){
            binder.cartRecyclerView.apply {
                listAdapter = ProductListAdapter(
                    viewModel.cart.items ?: arrayListOf(),
                    CART_ITEM_TYPE,
                    viewModel.cartMap
                )
                binder.cartRecyclerView.itemAnimator = null
                listAdapter.onClickListener = _itemClickListener
                binder.cartRecyclerView.adapter=listAdapter
            }
        }else{
            binder.tvEmptyCart.visible(true)
            binder.ivEmptyImage.visible(true)
        }


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
            binder.checkoutContainer.visible(total > 0)
        }
        if(viewModel.cart.items?.isEmpty()!=false){
            binder.tvEmptyCart.visible(true)
            binder.ivEmptyImage.visible(true)
        }
    }


    private fun initCart() {
       viewModel.cart = pref.get(CART)?: Cart()
        viewModel.initCart()
    }

}