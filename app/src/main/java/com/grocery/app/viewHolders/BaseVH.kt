package com.grocery.app.viewHolders

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseVH<Binder : ViewDataBinding, Data>(binder: Binder) :
    RecyclerView.ViewHolder(binder.root) {

    abstract fun bind(data: Data)
}