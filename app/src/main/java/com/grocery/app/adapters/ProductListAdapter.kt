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
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.models.Product
import com.grocery.app.viewHolders.BaseVH
import com.grocery.app.viewHolders.CartItemVH
import com.grocery.app.viewHolders.OrderItemVH
import com.grocery.app.viewModels.ProductGridVH

class ProductListAdapter(
    val products: ArrayList<Product>,
    private val itemType: Int,
    private val cartMap: HashMap<String, Product?> = hashMapOf()
) :
    RecyclerView.Adapter<BaseVH<*, Product>>() {

    var onClickListener: OnItemClickListener? = null
    val items
        get() = products

    override fun getItemViewType(position: Int): Int {
        return itemType
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun update(arrayList: java.util.ArrayList<Product>) {
        products.addAll(arrayList)
        notifyDataSetChanged()
    }

    fun clearAdapter() {
        products.clear()
        notifyDataSetChanged()
    }

    inner class ProductItemVH(private val binder: ProductListItemBinding) :
        BaseVH<ProductListItemBinding, Product>(binder), View.OnClickListener {


        override fun bind(data: Product) {
            binder.name = data.name
            binder.url = data.url
            binder.description = data.description
            binder.placeholder = R.drawable.category_placeholder
            binder.price = "Price: ${data.price}"
            binder.editIv.setOnClickListener(this)
            binder.executePendingBindings()

        }

        override fun onClick(v: View?) {
            onClickListener?.onItemClick(v?.id ?: -1, adapterPosition)
        }

    }

    override fun onBindViewHolder(holder: BaseVH<*, Product>, position: Int) {
        holder.bind(products[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVH<*, Product> {

        val viewHolder = when (viewType) {
            ADMIN_PRODUCT_TYPE -> {
                val binder = ProductListItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                ProductItemVH(binder)
            }
            HOMEPAGE_PRODUCT_TYPE -> {
                val binder = ProductItemWithPriceBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                ProductGridVH(binder).apply { cartMap = this@ProductListAdapter.cartMap }
            }
            CART_ITEM_TYPE -> {
                val binder = CartItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                CartItemVH(binder,cartMap)
            }
            ORDER_ITEMS -> {
                val binder = OrderItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                OrderItemVH(binder,cartMap)

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
}
