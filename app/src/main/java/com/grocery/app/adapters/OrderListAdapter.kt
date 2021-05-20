package com.grocery.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.databinding.AdminOrderListItemBinding
import com.grocery.app.databinding.ProgressBarItemBinding
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.models.Order
import com.grocery.app.viewHolders.BaseVH
import com.grocery.app.viewHolders.LoadMoreVH
import com.grocery.app.viewHolders.OrderVH

class OrderListAdapter(private var orders: ArrayList<Order>) :
    RecyclerView.Adapter<BaseVH<*, *>>() {

    companion object {
        const val ORDER_ITEM_TYPE = 234
        const val LOADER_ITEM_TYPE = 235
    }

    val items
        get() = orders

    private var loading = false

    var itemClickListener: OnItemClickListener? = null

    override fun getItemViewType(position: Int): Int {
        return if (position == orders.size)
            LOADER_ITEM_TYPE
        else ORDER_ITEM_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVH<*, *> {
        if (viewType == LOADER_ITEM_TYPE) {
            val binder = ProgressBarItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return LoadMoreVH(binder)
        } else {
            val binder = AdminOrderListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return OrderVH(binder).apply {
                itemClickListener = this@OrderListAdapter.itemClickListener
            }

        }
    }

    override fun onBindViewHolder(holder: BaseVH<*, *>, position: Int) {
        if (holder is OrderVH) {
            holder.bind(orders[position])
        }

    }

    fun addLoader() {
        if (!loading) {
            loading = true
            notifyItemInserted(orders.size)
        }
    }

    fun removeLoader() {
        if (loading) {
            loading = false
            notifyItemRemoved(orders.size)
        }
    }

    override fun getItemCount(): Int {
        return orders.size + if (loading) 1 else 0
    }

    fun update(addMore: Boolean = false, data: ArrayList<Order>) {
        if (!addMore) {
            orders = data
            loading = false
            notifyDataSetChanged()
            return
        }
        removeLoader()
        val oldSize = orders.size
        orders.addAll(data)
        notifyItemRangeInserted(oldSize, data.size)
    }
}