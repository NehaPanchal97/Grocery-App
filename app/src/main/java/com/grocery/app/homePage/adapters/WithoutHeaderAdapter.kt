package com.grocery.app.homePage.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.constant.OFFER_PAGE
import com.grocery.app.constant.WITHOUT_HEADER_HOME_PAGE
import com.grocery.app.databinding.CardviewWithoutHeaderBinding
import com.grocery.app.databinding.OfferPageItemBinding
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.models.Category
import com.grocery.app.viewHolders.BaseVH
import com.grocery.app.viewHolders.OfferPageVH
import com.grocery.app.viewHolders.WithoutHeaderItemVH


//Adapter for 1 screen 2 row
class WithoutHeaderAdapter( var itemList: ArrayList<Category>, private val itemType: Int) :
    RecyclerView.Adapter<BaseVH<*,Category>>() {

    var itemClickListener : OnItemClickListener?=null


    override fun onBindViewHolder(holder:BaseVH<*,Category> , position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun update(data: ArrayList<Category>) {
        itemList = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVH<*, Category> {
         when (viewType) {
            WITHOUT_HEADER_HOME_PAGE ->{
                val binder = CardviewWithoutHeaderBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                val holder = WithoutHeaderItemVH(binder)
                holder.itemClickListener=itemClickListener
                return holder
            }
            OFFER_PAGE->{
                val binder = OfferPageItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                val holder=OfferPageVH(binder)
                holder.itemClickListener = itemClickListener
                return holder
            }
            else->{
                val binder = CardviewWithoutHeaderBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                val holder = WithoutHeaderItemVH(binder)
                holder.itemClickListener=itemClickListener
                return holder
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return itemType
    }

}