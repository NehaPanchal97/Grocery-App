package com.grocery.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.grocery.app.R
import com.grocery.app.adapters.OrderListAdapter
import com.grocery.app.databinding.FragmentAdminOrderListBinding
import com.grocery.app.extensions.hide
import com.grocery.app.extensions.visible
import com.grocery.app.extras.Result
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        setupView()
        observe()
        viewModel.fetchOrders()
    }

    private fun observe() {
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

    private fun setupView() {
        listAdapter = OrderListAdapter(arrayListOf())
        binder.itemRv.adapter = listAdapter

        binder.emptyView.emptyTv.text = getString(R.string.no_order_available_msg)
    }
}