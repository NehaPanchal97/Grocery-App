package com.grocery.app.viewHolders

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.listeners.OnCategoryClickListener
import com.grocery.app.listeners.OnItemClickListener

abstract class BaseVH<Binder : ViewDataBinding, Data>(binder: Binder) :
    RecyclerView.ViewHolder(binder.root) {

    abstract fun bind(data: Data)
    open var itemClickListener: OnItemClickListener? = null
    open var categoryClickListener:OnCategoryClickListener?=null
}