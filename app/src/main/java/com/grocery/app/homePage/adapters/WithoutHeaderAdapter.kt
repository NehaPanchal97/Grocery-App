package com.grocery.app.homePage.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.databinding.CardviewWithoutHeaderBinding
import com.grocery.app.models.Category
import com.grocery.app.viewHolders.WithoutHeaderItemVH


//Adapter for 1 screen 2 row
class WithoutHeaderAdapter(private val itemList: ArrayList<Category>) :
    RecyclerView.Adapter<WithoutHeaderItemVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WithoutHeaderItemVH {
       val binder = CardviewWithoutHeaderBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return WithoutHeaderItemVH(binder)
    }

    override fun onBindViewHolder(holder: WithoutHeaderItemVH, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}