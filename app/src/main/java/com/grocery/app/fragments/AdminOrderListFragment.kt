package com.grocery.app.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.grocery.app.R
import com.grocery.app.activities.AdminOrderDetailActivity
import com.grocery.app.adapters.OrderListAdapter
import com.grocery.app.constant.ORDER
import com.grocery.app.constant.ORDER_CHANGE
import com.grocery.app.databinding.FragmentAdminOrderListBinding
import com.grocery.app.extensions.hide
import com.grocery.app.extensions.visible
import com.grocery.app.extras.Result
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.viewModels.OrderViewModel

class AdminOrderListFragment : BaseFragment() {

    private lateinit var binder: FragmentAdminOrderListBinding
    private lateinit var viewModel: OrderViewModel
    private lateinit var listAdapter: OrderListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binder = FragmentAdminOrderListBinding.inflate(inflater, container, false)
        return binder.root
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.orderUpdated) {
            viewModel.orderUpdated = false
            viewModel.fetchOrders()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        setupView()
        observe()
        viewModel.fetchOrders()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(_receiver)
    }

    private val _receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ORDER_CHANGE) {
                if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                    viewModel.fetchOrders()
                } else {
                    viewModel.orderUpdated = true
                }
            }
        }

    }

    private fun observe() {
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(_receiver, IntentFilter(ORDER_CHANGE))

        viewModel.orderListLiveData.observe(viewLifecycleOwner, Observer {
            when (it.type) {
                Result.Status.LOADING -> {
                    loading(true)
                }
                Result.Status.SUCCESS -> {
                    loading(false)
                    val orders = it.data ?: arrayListOf()
                    listAdapter.update(orders)
                    binder.emptyView.root.visible(orders.isEmpty())
                }
                Result.Status.ERROR -> {
                    loading(false)
                }
            }
        })
    }

    override fun loading(show: Boolean) {
        binder.progressBar.visible(show)
        if (show) {
            binder.emptyView.root.hide()
        }
    }

    private val _itemClickListener = object : OnItemClickListener {
        override fun onItemClick(itemId: Int, position: Int) {
            listAdapter.items.getOrNull(position)?.let {
                val i = Intent(requireContext(), AdminOrderDetailActivity::class.java).apply {
                    putExtra(ORDER, it)
                }
                startActivity(i)
            }

        }
    }

    private fun setupView() {
        listAdapter = OrderListAdapter(arrayListOf())
        listAdapter.itemClickListener = _itemClickListener
        binder.itemRv.adapter = listAdapter

        binder.emptyView.emptyTv.text = getString(R.string.no_order_available_msg)
    }
}