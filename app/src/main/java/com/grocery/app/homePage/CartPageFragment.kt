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
import com.grocery.app.constant.USER
import com.grocery.app.databinding.CartItemsGroupBinding
import com.grocery.app.extensions.showError
import com.grocery.app.extensions.showSuccess
import com.grocery.app.extensions.visible
import com.grocery.app.fragments.BaseFragment
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.models.Cart
import com.grocery.app.models.User
import com.grocery.app.utils.OrderUtils
import com.grocery.app.utils.PrefManager
import com.grocery.app.viewModels.OrderViewModel
import com.grocery.app.viewModels.ProductViewModel

private typealias Status=com.grocery.app.extras.Result.Status


class CartPageFragment : BaseFragment() {

    lateinit var binder: CartItemsGroupBinding
    private lateinit var listAdapter: ProductListAdapter
    lateinit var viewModel: ProductViewModel
    lateinit var orderViewModel: OrderViewModel
    lateinit var pref: PrefManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder.cartRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        orderViewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        setUpView()
        listener()
        observe()
    }

    private fun listener(){
        val closeBtn =binder.cartBackBtn
        closeBtn.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun observe() {
        orderViewModel.updateOrderLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it.type) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {
                    pref.remove(CART)
                    viewModel.resetCart()
                    listAdapter.clearAdapter()
                    onTotalChange()
                    binder.root.showSuccess(getString(R.string.order_created_msg))
                }
                Status.ERROR -> {
                    binder.root.showError(getString(R.string.order_create_error))
                }
            }
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binder = CartItemsGroupBinding.inflate(inflater, container, false)
        return binder.root
    }

    private fun createOrder() {
        val user = pref.get(USER) ?: User()
        val order = OrderUtils.createOrder(
            requireContext(),
            viewModel.cart.items ?: arrayListOf(),
            viewModel.cart.total ?: 0.0,
            user.id ?: "",
            user.name ?: "",
            user.phone ?: "",
            user.address ?: ""
        )
        orderViewModel.createOrder(order, viewModel.cart.id)
    }

    @SuppressLint("SetTextI18n")
    private fun setUpView() {
        pref = PrefManager.getInstance(requireContext())
        initCart()
        binder.checkoutBtn.setOnClickListener {
            createOrder()
        }
        val cartTotal = viewModel.cart.total?.toString() ?: "0"
        binder.cartAmount.text = "Total : \$$cartTotal"
        val total = viewModel.cart.total

        if (total != null) {
            binder.checkoutContainer.visible(total > 0)
        }
        if (viewModel.cart.items?.isEmpty() != true) {
            binder.cartRecyclerView.apply {
                listAdapter = ProductListAdapter(
                    viewModel.cart.items ?: arrayListOf(),
                    CART_ITEM_TYPE,
                    viewModel.cartMap
                )
                binder.cartRecyclerView.itemAnimator = null
                listAdapter.onClickListener = _itemClickListener
                binder.cartRecyclerView.adapter = listAdapter
            }
        } else{
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
    private fun onTotalChange() {
        val cartTotal = viewModel.cart.total?.toString() ?: "0"
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
        viewModel.cart = pref.get(CART) ?: Cart()
        viewModel.initCart()
    }

}