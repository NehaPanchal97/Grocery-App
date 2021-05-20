package com.grocery.app.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.grocery.app.R
import com.grocery.app.adapters.ProductListAdapter
import com.grocery.app.constant.*
import com.grocery.app.databinding.ActivityAdminOrderDetailBinding
import com.grocery.app.exceptions.OrderStatusChangeException
import com.grocery.app.extensions.showError
import com.grocery.app.extensions.showSuccess
import com.grocery.app.extensions.visible
import com.grocery.app.extras.Result
import com.grocery.app.models.Cart
import com.grocery.app.models.Order
import com.grocery.app.viewModels.OrderViewModel

class AdminOrderDetailActivity : BaseActivity() {

    private lateinit var binder: ActivityAdminOrderDetailBinding
    private lateinit var listAdapter: ProductListAdapter
    private lateinit var viewModel: OrderViewModel

    private var _order
        get() = viewModel.order
        set(value) {
            viewModel.order = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_admin_order_detail)
        viewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        setupView()
        observe()
    }

    private fun observe() {
        viewModel.updateOrderLiveData.observe(this, Observer {
            when (it.type) {
                Result.Status.LOADING -> loading(true)
                Result.Status.SUCCESS -> {
                    loading(false)
                    onActionChange()
                    binder.root.showSuccess(getString(R.string.order_status_changed_msg))
                }
                Result.Status.ERROR -> {
                    loading(false)
                    if (it.exception is OrderStatusChangeException) {
                        onActionChange()
                    } else {
                        binder.root.showError(getString(R.string.unable_to_change_status_error))
                    }
                }
            }
        })
    }

    override fun loading(show: Boolean) {
        binder.progressBar.visible(show)
        disableField(show)
    }

    private fun disableField(disable: Boolean) {
        binder.actionBtn.isEnabled = !disable
    }

    private fun setupView() {
        setupToolbar(binder.toolBar)
        _order = intent?.getParcelableExtra(ORDER) ?: Order()
        listAdapter = ProductListAdapter(
            _order.items ?: arrayListOf(),
            ADMIN_ORDER_PRODUCT_ITEM_TYPE
        )
        binder.productRv.adapter = listAdapter
        onActionChange(true)
        binder.totalTv.text = getString(R.string.rs, _order.payableAmount?.toString())
        binder.actionBtn.setOnClickListener {
            showConfirmationAlert()
        }
    }

    private fun showConfirmationAlert() {
        val nextStatus = _order.allStatus?.firstOrNull { it.completed == false }
        MaterialAlertDialogBuilder(this, R.style.AppDialogTheme)
            .setTitle(R.string.are_you_sure)
            .setMessage(getString(R.string.order_status_move_msg, nextStatus?.status))
            .setPositiveButton(
                R.string.yes
            ) { _, _ -> viewModel.updateOrder() }
            .setNegativeButton(R.string.no) { _, _ -> }
            .show()
    }

    private fun onActionChange(initialCall: Boolean = false) {
        val nextStatus = _order.allStatus?.firstOrNull { it.completed == false }
        val hideActionBtn = _order.currentStatus in
                setOf(OrderStatus.CANCELLED.title, OrderStatus.DELIVERED.title)

        val orderBgColor =
            if (_order.currentStatus == OrderStatus.CANCELLED.title) R.color.error_color
            else R.color.success_color
        binder.orderStatusTv.visible(hideActionBtn)
        binder.orderStatusTv.text = _order.currentStatus?.capitalize()
        binder.orderStatusTv.setBackgroundColor(ContextCompat.getColor(this, orderBgColor))

        binder.actionBtn.visible(!hideActionBtn)
        binder.actionBtn.text =
            getString(R.string.move_order_msg, nextStatus?.status?.capitalize())
        if (!initialCall) {
            fireOrderChangeEvent()
        }
    }

    private fun fireOrderChangeEvent() {
        val i = Intent(ORDER_CHANGE)
        LocalBroadcastManager.getInstance(this)
            .sendBroadcast(i)
    }
}