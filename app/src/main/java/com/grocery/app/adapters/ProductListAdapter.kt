package com.grocery.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R
import com.grocery.app.constant.*
import com.grocery.app.databinding.CartItemBinding
import com.grocery.app.databinding.OrderItemBinding
import com.grocery.app.databinding.ProductItemWithPriceBinding
import com.grocery.app.databinding.ProductListItemBinding
import com.grocery.app.constant.*
import com.grocery.app.databinding.*
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.models.Product
import com.grocery.app.viewHolders.*
import com.grocery.app.viewModels.ProductGridVH

class ProductListAdapter(
    var products: ArrayList<Product>,
    private val itemType: Int,
    private val cartMap: HashMap<String, Product?> = hashMapOf()
) :
    RecyclerView.Adapter<BaseVH<*, Product>>() {

    companion object {
        const val LOAD_MORE_ITEM_TYPE = 1002
    }

    var onClickListener: OnItemClickListener? = null
    val items
        get() = products

    private var loading = false

    override fun getItemViewType(position: Int): Int {
        return if (position == products.size)
            LOAD_MORE_ITEM_TYPE
        else itemType
    }

    override fun getItemCount(): Int {
        return products.size + if (loading) 1 else 0
    }

    fun update(addMore: Boolean = false, arrayList: ArrayList<Product>) {
        if (!addMore) {
            products = arrayList
            notifyDataSetChanged()
            return
        }
        removeLoader()
        val oldSize = products.size
        products.addAll(arrayList)
        notifyItemRangeInserted(oldSize, arrayList.size)
    }

    fun addLoader() {
        if (!loading) {
            loading = true
            notifyItemInserted(products.size)
        }
    }

    private fun removeLoader() {
        if (loading) {
            loading = false
            notifyItemRemoved(products.size)
        }
    }

    fun clearAdapter() {
        products.clear()
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: BaseVH<*, Product>, position: Int) {
        if (holder !is LoadMoreVH) {
            holder.bind(products[position])
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVH<*, Product> {

        val viewHolder = when (viewType) {
            ADMIN_PRODUCT_TYPE -> {
                val binder = ProductListItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                AdminProductItemVH(binder).apply { itemClickListener = onClickListener }
            }
            HOMEPAGE_PRODUCT_TYPE -> {
                val binder = ProductItemWithPriceBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                ProductGridVH(binder).apply { cartMap = this@ProductListAdapter.cartMap }
            }
            CART_ITEM_TYPE -> {
                val binder = CartItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                CartItemVH(binder, cartMap)
            }
            ADMIN_ORDER_PRODUCT_ITEM_TYPE -> {
                val binder = AdminOrderProductItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                AdminOrderProductVH(binder)
            }
            ORDER_DESCRIPTION_ITEM_TYPE -> {
                val binder = OrderDescriptionItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                OrderDescriptionItemVH(binder)
            }
            LOAD_MORE_ITEM_TYPE -> {
                val binder = ProgressBarItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                LoadMoreVH(binder)
            }

            else -> {
                val binder = ProductItemWithPriceBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                ProductGridVH(binder)
            }
        }
        viewHolder.itemClickListener = onClickListener
        return viewHolder
    }

    private inner class LoadMoreVH(binder: ProgressBarItemBinding) :
        BaseVH<ProgressBarItemBinding, Product>(binder) {

        override fun bind(data: Product) {}

    }
}
