package com.grocery.app.homePage

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R
import com.grocery.app.activities.DetailsPageActivity
import com.grocery.app.adapters.ProductListAdapter
import com.grocery.app.constant.*
import com.grocery.app.customs.OnLoadMoreListener
import com.grocery.app.databinding.ProductItemgroupLayoutBinding
import com.grocery.app.extensions.showError
import com.grocery.app.extras.Result
import com.grocery.app.fragments.BaseFragment
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.models.Cart
import com.grocery.app.utils.PrefManager
import com.grocery.app.viewModels.CategoryViewModel
import com.grocery.app.viewModels.ProductViewModel

class ProductListFragment : BaseFragment() {

    private lateinit var itemRecyclerViewAdapter: ProductListAdapter
    lateinit var binder: ProductItemgroupLayoutBinding
    private lateinit var viewModel: ProductViewModel
    private lateinit var catViewModel: CategoryViewModel

    private val pref by lazy { PrefManager.getInstance(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder.itemRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        binder.itemRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.HORIZONTAL
            )
        )
        binder.itemRecyclerView.layoutManager =
            GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        catViewModel = ViewModelProvider(requireActivity()).get(CategoryViewModel::class.java)
        initCart()
        itemRecyclerView()
        observe()
        viewModel.filterByCat = arguments?.getParcelable(CATEGORY)
        binder.specificCategory.text = viewModel.filterByCat?.name
        viewModel.fetchProductList()

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
        itemRecyclerViewAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(_receiver)
    }

    private fun initCart() {
        viewModel.cart = pref.get(CART) ?: Cart()
        viewModel.initCart()
    }

    private val _receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: ""
            if (action in setOf(CART_CHANGE, ORDER_CREATED)) {
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
        filter.addAction(ORDER_CREATED)
        filter.addAction(CART_CHANGE)
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(_receiver, filter)
        viewModel.productListLiveData.observe(viewLifecycleOwner, Observer {
            when (it.type) {
                Result.Status.LOADING -> {
                    binder.progressBar.show()
                }
                Result.Status.SUCCESS -> {
                    binder.progressBar.hide()
                    val products = it.data ?: arrayListOf()
                    itemRecyclerViewAdapter.update(false, products)
                }

                Result.Status.ERROR -> {
                    binder.progressBar.hide()
                    binder.root.showError("unable to fetch products")
                }

            }
        })
        viewModel.updateCart.observe(viewLifecycleOwner, Observer {
            if (it.type == Result.Status.SUCCESS) {
                pref.put(CART, viewModel.cart)
//                itemRecyclerViewAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binder = ProductItemgroupLayoutBinding.inflate(inflater, container, false)
        return binder.root
    }

    private val _itemClickListener = object : OnItemClickListener {
        override fun onItemClick(itemId: Int, position: Int) {
            val product = itemRecyclerViewAdapter.items.getOrNull(position)
            if (itemId == R.id.iv_add) {
                viewModel.updateCart(product, isAddition = true)
                itemRecyclerViewAdapter.notifyItemChanged(position)

            } else if (itemId == R.id.iv_remove) {
                viewModel.updateCart(product, isAddition = false)
                itemRecyclerViewAdapter.notifyItemChanged(position)
            } else if (itemId == R.id.itemImage) {
                val intent = Intent(requireContext(), DetailsPageActivity::class.java)
                intent.putExtra(PRODUCT, product)
                startActivity(intent)
            }
        }

    }


    private fun itemRecyclerView() {


        binder.itemRecyclerView.apply {
            itemRecyclerViewAdapter = ProductListAdapter(
                arrayListOf(),
                HOMEPAGE_PRODUCT_TYPE, viewModel.cartMap
            )
            itemRecyclerViewAdapter.onClickListener = _itemClickListener
            binder.itemRecyclerView.adapter = itemRecyclerViewAdapter
        }
    }

}