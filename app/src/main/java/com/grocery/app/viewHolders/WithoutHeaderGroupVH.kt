package com.grocery.app.viewHolders

import androidx.recyclerview.widget.LinearLayoutManager
import com.grocery.app.constant.WITHOUT_HEADER_HOME_PAGE
import com.grocery.app.databinding.WithoutHeaderRvGroupBinding
import com.grocery.app.homePage.adapters.WithoutHeaderAdapter
import com.grocery.app.homePage.dataModel.ItemGroup
import com.grocery.app.listeners.OnCategoryClickListener
import com.grocery.app.listeners.OnItemClickListener

class WithoutHeaderGroupVH(private val binder: WithoutHeaderRvGroupBinding,private val onItemClickListener: OnCategoryClickListener?) :
        BaseVH<WithoutHeaderRvGroupBinding, ItemGroup>(binder) {
    //horizontal recycler view without header of homepage i.e 2 row


    override fun bind(data: ItemGroup) {
        val itemListAdapter = data.listItem?.let { WithoutHeaderAdapter(ArrayList(it), WITHOUT_HEADER_HOME_PAGE).apply {
            itemClickListener=onItemClickListener
        } }

        binder.withoutHeaderRv.setHasFixedSize(true)
        binder.withoutHeaderRv.layoutManager =
                LinearLayoutManager(binder.root.context, LinearLayoutManager.HORIZONTAL, false)
        binder.withoutHeaderRv.adapter = itemListAdapter

        binder.withoutHeaderRv.isNestedScrollingEnabled = false


    }




}