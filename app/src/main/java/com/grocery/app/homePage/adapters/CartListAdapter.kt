package com.grocery.app.homePage.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.databinding.CartItemBinding
import com.grocery.app.models.Product
import com.grocery.app.viewHolders.BaseVH

class CartListAdapter(val products:ArrayList<Product>):
        RecyclerView.Adapter<BaseVH<*, Product>>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVH<*, Product> {
        val binder = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
       return CartItemVH(binder)
    }

    override fun onBindViewHolder(holder: BaseVH<*, Product>, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int {
        return products.size
    }

    inner class CartItemVH(private val binder:CartItemBinding):
            BaseVH<CartItemBinding,Product>(binder){
        override fun bind(data: Product) {


        }

    }

}