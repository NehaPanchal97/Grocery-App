package com.grocery.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R
import com.grocery.app.databinding.ProductListItemBinding
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.models.Product
import com.grocery.app.viewHolders.BaseVH

class ProductListAdapter(val products: ArrayList<Product>) :
    RecyclerView.Adapter<ProductListAdapter.ProductItemVH>() {

    var onClickListener: OnItemClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductItemVH {
        val binder = ProductListItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductItemVH(binder)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ProductItemVH, position: Int) {
        holder.bind(products[position])
    }

    fun update(arrayList: java.util.ArrayList<Product>) {
        products.addAll(arrayList)
        notifyDataSetChanged()
    }

    fun clearAdapter(){
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
}
