package com.grocery.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R
import com.grocery.app.constant.ADMIN_PRODUCT_TYPE
import com.grocery.app.constant.HOMEPAGE_PRODUCT_TYPE
import com.grocery.app.databinding.ProductItemWithPriceBinding
import com.grocery.app.databinding.ProductListItemBinding
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.models.Product
import com.grocery.app.viewHolders.BaseVH
import com.grocery.app.viewModels.ProductGridVH

class ProductListAdapter(val products: ArrayList<Product>, private val itemType: Int) :
    RecyclerView.Adapter<BaseVH<*, Product>>() {

    var onClickListener: OnItemClickListener? = null

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

        return if (viewType == ADMIN_PRODUCT_TYPE) {
            val binder = ProductListItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            ProductItemVH(binder)
        } else if (viewType == HOMEPAGE_PRODUCT_TYPE) {
            val binder = ProductItemWithPriceBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            ProductGridVH(binder)
        } else {
            val binder = ProductItemWithPriceBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            ProductGridVH(binder)
        }

    }
}
