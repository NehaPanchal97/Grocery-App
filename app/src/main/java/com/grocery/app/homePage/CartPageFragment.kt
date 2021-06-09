package com.grocery.app.homePage

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R
import com.grocery.app.adapters.ProductListAdapter
import com.grocery.app.constant.*
import com.grocery.app.databinding.CartItemsGroupBinding
import com.grocery.app.extensions.cast
import com.grocery.app.extensions.showError
import com.grocery.app.extensions.trim
import com.grocery.app.extensions.visible
import com.grocery.app.fragments.BaseFragment
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.models.Cart
import com.grocery.app.models.Product
import com.grocery.app.models.User
import com.grocery.app.services.OrderChangeService
import com.grocery.app.utils.OrderUtils
import com.grocery.app.utils.PrefManager
import com.grocery.app.viewModels.OrderViewModel
import com.grocery.app.viewModels.ProductViewModel
import okhttp3.internal.notify

private typealias Status = com.grocery.app.extras.Result.Status


class CartPageFragment : BaseFragment() {

    lateinit var binder: CartItemsGroupBinding
    private lateinit var listAdapter: ProductListAdapter
    lateinit var viewModel: ProductViewModel
    lateinit var orderViewModel: OrderViewModel
    lateinit var pref: PrefManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binder.cartRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        binder.cartRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        orderViewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        setUpView()
        listener()
        observe()
    }

    private fun listener() {
        val closeBtn = binder.cartBackBtn
        closeBtn.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.cartUpdated) {
            viewModel.cartUpdated = false
            resetCart()
        }
    }

    private fun resetCart() {
        val cart = pref.get(CART) ?: Cart()
        viewModel.initCartWith(cart)
        listAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(_receiver)
    }

    private val _receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: ""
            if (action in setOf(CART_CHANGE)) {
                if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                    resetCart()
                } else {
                    viewModel.cartUpdated = true
                }
            }
        }

    }

    private fun observe() {
        val filter = IntentFilter()
        filter.addAction(CART_CHANGE)
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(_receiver, filter)

        orderViewModel.updateOrderLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it.type) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {
                    val orderId = orderViewModel.order.id
                    fireOrderCreatedEvent()
                    val intent = Intent(activity, OrderStatusPageActivity::class.java)
                    intent.putExtra(ORDER_ID, orderId)
                    startActivity(intent)
                    pref.remove(CART)
                    viewModel.resetCart()
                    onTotalChange()
                    fireCartChangeEvent()
                    if (orderViewModel.orderCreated) {
                        orderViewModel.orderCreated = false
                        onOrderCreated()
                    }
                    activity?.cast<HomePageActivity>()?.switchFragment()

                }
                Status.ERROR -> {
                    binder.root.showError(getString(R.string.order_create_error))
                }
            }
        })
    }

    private fun onOrderCreated() {
        context?.let {
            val order = orderViewModel.order
            val i = OrderChangeService.getIntent(it, order)
            it.startService(i)
        }
    }

    private fun fireOrderCreatedEvent() {
        LocalBroadcastManager.getInstance(requireContext())
            .sendBroadcast(Intent(ORDER_CREATED))
    }

    private fun fireCartChangeEvent() {
        LocalBroadcastManager.getInstance(requireContext())
            .sendBroadcast(Intent(CART_CHANGE))
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
            user.address ?: "",
            viewModel.cart.totalDiscount ?: 0.0,
            viewModel.cart.payableAmount ?: 0.0
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
        onTotalChange()
        binder.cartRecyclerView.apply {
            listAdapter = ProductListAdapter(
                viewModel.cart.items ?: arrayListOf(),
                CART_ITEM_TYPE,
                viewModel.cartMap
            )
            binder.cartRecyclerView.itemAnimator = null
            listAdapter.onClickListener = _itemClickListener
            binder.cartRecyclerView.adapter = listAdapter
            val swipeDelete = object :SwipeToDeleteCallback(){
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val product = listAdapter.items[viewHolder.bindingAdapterPosition]
                    listAdapter.deleteItem(viewHolder.bindingAdapterPosition)
                    viewModel.updateCart(product,CartAction.ITEM_REMOVED)
                    listAdapter.update(false,viewModel.cart.items?: arrayListOf())
                    onTotalChange()
                    pref.put(CART, viewModel.cart)
                    fireCartChangeEvent()
                }
            }
            val touchHelper =ItemTouchHelper(swipeDelete)
            touchHelper.attachToRecyclerView(this)
        }
    }


    private val _itemClickListener = object : OnItemClickListener {
        @SuppressLint("SetTextI18n")
        override fun onItemClick(itemId: Int, position: Int) {
            if (itemId == R.id.iv_cart_add) {
                val product = listAdapter.items.getOrNull(position)
                viewModel.updateCart(product)
                listAdapter.notifyItemChanged(position)
                onTotalChange()
                pref.put(CART, viewModel.cart)
                fireCartChangeEvent()
            } else if (itemId == R.id.iv_cart_remove) {
                val product = listAdapter.items.getOrNull(position)
                viewModel.updateCart(product,CartAction.QUANTITY_DECREASED)
                listAdapter.update(arrayList = viewModel.cart.items ?: arrayListOf())
                onTotalChange()
                pref.put(CART, viewModel.cart)
                fireCartChangeEvent()
            }

        }

    }


    @SuppressLint("SetTextI18n", "StringFormatMatches")
    private fun onTotalChange() {
        val cartTotal = viewModel.cart.payableAmount?.trim
        binder.cartAmount.text = getString(R.string.cart_total, cartTotal)
        if (viewModel.cart.items?.isEmpty() != false) {
            binder.tvEmptyCart.visible(true)
            binder.ivEmptyImage.visible(true)
            binder.checkoutContainer.visible(false)
        }
    }


    private fun initCart() {
        viewModel.cart = pref.get(CART) ?: Cart()
        viewModel.initCart()
    }

}