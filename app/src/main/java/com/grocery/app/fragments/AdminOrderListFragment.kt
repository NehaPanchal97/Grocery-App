package com.grocery.app.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.grocery.app.R
import com.grocery.app.activities.AdminOrderDetailActivity
import com.grocery.app.adapters.OrderListAdapter
import com.grocery.app.constant.ORDER
import com.grocery.app.constant.ORDER_CHANGE
import com.grocery.app.constant.OrderStatus
import com.grocery.app.customs.OnLoadMoreListener
import com.grocery.app.databinding.FragmentAdminOrderListBinding
import com.grocery.app.extensions.hide
import com.grocery.app.extensions.visible
import com.grocery.app.extras.Result
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.viewModels.OrderViewModel
import java.util.*

class AdminOrderListFragment : BaseFragment(), Toolbar.OnMenuItemClickListener {

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
                    if (viewModel.loadingMore) {
                        listAdapter.addLoader()
                    } else {
                        loading(true)
                    }
                }
                Result.Status.SUCCESS -> {
                    if (!viewModel.loadingMore) {
                        loading(false)
                    }
                    val orders = it.data ?: arrayListOf()
                    listAdapter.update(addMore = viewModel.loadingMore, data = orders)
                    binder.emptyView.root.visible(listAdapter.items.isEmpty())
                }
                Result.Status.ERROR -> {
                    if (viewModel.loadingMore) {
                        listAdapter.removeLoader()
                    } else {
                        loading(false)
                    }
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

    private val _onLoadMoreListener = object : OnLoadMoreListener() {

        override val hasMore: Boolean
            get() = viewModel.hasMoreOrder

        override val isRequesting: Boolean
            get() = viewModel.orderListLiveData.value?.type == Result.Status.LOADING

        override fun onLoadMore() {
            viewModel.fetchOrders(initialFetch = false)
        }

    }

    private fun setupView() {
        listAdapter = OrderListAdapter(arrayListOf())
        listAdapter.itemClickListener = _itemClickListener
        binder.itemRv.adapter = listAdapter
        binder.itemRv.addOnScrollListener(_onLoadMoreListener)

        binder.emptyView.emptyTv.text = getString(R.string.no_order_available_msg)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.filter) {
            showFilterDialog()
        }
        return true
    }

    private fun showFilterDialog() {

        val items = OrderStatus.values().filter { it != OrderStatus.OTHER }
            .map { it.title.capitalize() }.toTypedArray()
        val checked =
            items.indexOfFirst { it.toLowerCase(Locale.ENGLISH) == viewModel.filterOrderByStatus }
        MaterialAlertDialogBuilder(requireContext(), R.style.AppDialogTheme)
            .setTitle(getString(R.string.filter_by_status))
            .setNeutralButton(
                getString(R.string.clear)
            ) { _, _ ->
                viewModel.clearFilter()
                viewModel.fetchOrders(initialFetch = true)
            }
            .setPositiveButton(getString(R.string.apply)) { _, _ ->
                viewModel.filterOrderByStatus?.let {
                    viewModel.fetchOrders(initialFetch = true)
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                viewModel.filterOrderByStatus = if (checked > -1)
                    items[checked].toLowerCase(Locale.ENGLISH)
                else null
            }
            .setSingleChoiceItems(items, checked) { _, which ->
                viewModel.filterOrderByStatus = items[which].toLowerCase(Locale.ENGLISH)
            }
            .setCancelable(false)
            .show()
    }
}