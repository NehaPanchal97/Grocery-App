package com.grocery.app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.adapters.OrderItemAdapter
import com.grocery.app.constant.ORDER_ID
import com.grocery.app.databinding.FragmentOrderBinding
import com.grocery.app.extensions.authUser
import com.grocery.app.extensions.visible
import com.grocery.app.extras.Result
import com.grocery.app.homePage.OrderStatusPageActivity
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.utils.PrefManager
import com.grocery.app.viewModels.OrderViewModel


class OrderFragment : BaseFragment(), OnItemClickListener {
    lateinit var binder: FragmentOrderBinding
    lateinit var viewModel: OrderViewModel
    lateinit var pref: PrefManager
    lateinit var listAdapter: OrderItemAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder.orderRv.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        setUpView()
        observeData()
        viewModel.fetchOrders()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binder = FragmentOrderBinding.inflate(inflater, container, false)
        return binder.root
    }

    private fun setUpView() {
        pref = PrefManager.getInstance(requireContext())
        binder.orderRv.apply {
            listAdapter = OrderItemAdapter(arrayListOf())
            binder.orderRv.adapter = listAdapter
        }
        viewModel.orderCreatedBy = authUser?.uid
        listAdapter.itemClickListener = this
    }


    private fun observeData() {
        viewModel.orderListLiveData.observe(viewLifecycleOwner, Observer {
            when (it.type) {
                Result.Status.LOADING -> {
                    orderEmptyView(false)
                }
                Result.Status.SUCCESS -> {
                    if (it.data?.isNotEmpty() == true) {
                        listAdapter.updateAdapterData(it.data)
                        orderEmptyView(false)
                    } else {
                        orderEmptyView(true)
                    }

                }
                Result.Status.ERROR -> {
                }

            }
        })
    }

    private fun orderEmptyView(show: Boolean) {
        binder.noOrderView.visible(show)
    }

    override fun onItemClick(itemId: Int, position: Int) {
        val order = listAdapter.items[position]
        val intent = Intent(activity, OrderStatusPageActivity::class.java)
        intent.putExtra(ORDER_ID, order.id)
        startActivity(intent)

    }


}